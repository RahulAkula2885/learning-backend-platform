package in.rahul.learning.service;

import com.google.common.hash.BloomFilter;
import in.rahul.learning.commons.BaseResponse;
import in.rahul.learning.config.BloomFilterConfig;
import in.rahul.learning.exceptions.CustomException;
import in.rahul.learning.filters.EmailBloomService;
import in.rahul.learning.mail.service.MailService;
import in.rahul.learning.mail.utils.MailTemplate;
import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.request.LoginRequest;
import in.rahul.learning.model.request.UserRequest;
import in.rahul.learning.model.response.UserResponse;
import in.rahul.learning.model.response.UserResponseXml;
import in.rahul.learning.repo.IUserRepository;
import in.rahul.learning.security.jwt.JWTTokenProvider;
import in.rahul.learning.security.model.UserPrinciple;
import in.rahul.learning.service.cache.UserServiceCache;
import in.rahul.learning.service.validations.UserValidations;
import in.rahul.learning.util.AESUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;

import static in.rahul.learning.commons.CommonMessages.*;
import static in.rahul.learning.security.jwt.JWTTokenProvider.JWT_TOKEN_HEADER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserValidations userValidations;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider jwtTokenProvider;
    private final AESUtil aesUtil;
    private final UserServiceCache userServiceCache;
    private final MailService mailService;
    private final MailTemplate mailTemplate;
    private final EmailBloomService emailBloomService;
    private final BloomFilter<String> emailBloomFilter;

    @Transactional
    @Override
    public ResponseEntity<BaseResponse> createUser(UserRequest request) {

        userValidations.checkCreateUserValidations(request);

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setPhoneNo(request.phoneNo());
        user.setActive(true);
        user.setDeleted(false);
        user.setCreatedTime(Instant.now());
        user.setModifiedTime(Instant.now());

        userServiceCache.saveUsers(user);

        // 3. Update Bloom Filter with redis cache
        //emailBloomService.add(user.getEmail());

        //this is for bloom filter
        emailBloomFilter.put(user.getEmail());

        UserPrinciple userPrinciple = new UserPrinciple(user, Set.of(user.getRole()));
        String token = jwtTokenProvider.generateJWTToken(userPrinciple);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, token);

        BaseResponse baseResponse = BaseResponse.builder()
                .status(200)
                .message(SUCCESS)
                .timestamp(Instant.now())
                .build();

        //send mail
        Map<String, Object> response = new HashMap<>();
        response.put("name",user.getName());
        response.put("email",user.getEmail());
        response.put("phone",user.getPhoneNo());
        try {
            mailService.sendMailTemplate(response,mailTemplate.welcomeEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new CustomException(e.getMessage());
        }

        return ResponseEntity.ok().headers(headers).body(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse> updateUser(User request) {

        User user = userValidations.checkUpdateUserValidations(request);
        userServiceCache.updateUsers(user);

        return ResponseEntity.ok(BaseResponse.builder()
                .status(200)
                .message(SUCCESS)
                .timestamp(Instant.now())
                .build());

    }

    @Override
    public List<UserResponse> getUserDetails() {
        LOGGER.info("Received request to get user details");
        return userServiceCache.findAllUserDetails();
        /*return userRepository.findAllByDeletedFalse()
                .stream()
                .map(user -> {
                    UserResponse response =  UserResponse.builder().build();
                    BeanUtils.copyProperties(user, response);
                    return response;
                })
                .toList();*/
    }

    @Override
    public UserResponse getUserDetailsById(Long id) {
        LOGGER.info("Received request to get user details by id {}", id);
        return userServiceCache.getUserDetailsById(id);
    }

    @Override
    public ResponseEntity<BaseResponse> login(LoginRequest request) {
        LOGGER.info("Received request to login {}", request);

        User user = userValidations.checkLoginValidation(request);
        UserResponse userResponse = mapToResponse(user);


        UserPrinciple userPrinciple = new UserPrinciple(user, Set.of(user.getRole()));
        String token = jwtTokenProvider.generateJWTToken(userPrinciple);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, token);

        Map<Object, Object> map = new HashMap<>();
        map.put("response",userResponse);
        map.put(JWT_TOKEN_HEADER,token);

        BaseResponse baseResponse = BaseResponse.builder()
                .status(200)
                .message(SUCCESS)
                .data(map)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok().headers(headers).body(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse> deleteUser(User request) {

        if (request.getId() == null || request.getId() <= 0) {
            throw new CustomException(ID_REQUIRED);
        }
        Optional<User> optionalUser = userRepository.findById(request.getId());
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setEmail(user.getEmail() + ".deleted");
        user.setActive(false);
        user.setDeleted(true);

        userServiceCache.updateUsers(user);

        return ResponseEntity.ok(BaseResponse.builder()
                .status(200)
                .message(SUCCESS)
                .timestamp(Instant.now())
                .build());
    }

    @Override
    public UserResponseXml getUserByIdXmlFormat(Long id) {
        LOGGER.info("Received request to get user details by id {}", id);
        return userServiceCache.getUserByIdXmlFormat(id);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getPhoneNo(),
                user.getActive(),
                user.getDeleted(),
                user.getCreatedTime(),
                user.getModifiedTime()
        );
    }
}
