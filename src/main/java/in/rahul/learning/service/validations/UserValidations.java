package in.rahul.learning.service.validations;

import com.google.common.hash.BloomFilter;
import in.rahul.learning.exceptions.CustomException;
import in.rahul.learning.filters.EmailBloomService;
import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.enums.UserRole;
import in.rahul.learning.model.request.LoginRequest;
import in.rahul.learning.model.request.UserRequest;
import in.rahul.learning.repo.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;

import static in.rahul.learning.commons.CommonMessages.*;

@Service
@RequiredArgsConstructor
public class UserValidations {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserValidations.class);

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailBloomService emailBloomService;
    private final BloomFilter<String> emailBloomFilter;

    public void checkCreateUserValidations(UserRequest request) {
        LOGGER.info("Received request to create user {}", request);

        if (!StringUtils.hasText(request.name())) {
            throw new CustomException(NAME_REQUIRED);
        }
        if (!StringUtils.hasText(request.phoneNo())) {
            throw new CustomException(PHONE_NUMBER_REQUIRED);
        }
        if (!StringUtils.hasText(request.email())) {
            throw new CustomException(EMAIL_REQUIRED);
        }
        if (!StringUtils.hasText(request.password())) {
            throw new CustomException(PASSWORD_REQUIRED);
        }
        if (!isValidEmail(request.email())) {
            throw new CustomException(INVALID_EMAIL_FORMAT);
        }
        if (!isStrongPassword(request.password())) {
            throw new CustomException(INVALID_PASSWORD_FORMAT);
        }
        if (!isValidRole(request.role())) {
            throw new CustomException(INVALID_ROLE);
        }
        // 1. Fast check (Bloom Filter)
        if (!emailBloomService.mightExist(request.email())) {

        //if (!emailBloomFilter.mightContain(request.email())) {
            // fallback to DB (because Bloom can false-positive)
            if (userRepository.existsByEmail(request.email())) {
                throw new CustomException(EMAIL_ALREADY_EXISTS);
            }
        }else{
            throw new CustomException(EMAIL_ALREADY_EXISTS + " bloom filter");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private boolean isStrongPassword(String password) {
        String passwordRegex =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(passwordRegex);
    }

    private boolean isValidRole(UserRole role) {
        try {
            UserRole.valueOf(role.toString());
            return true;
        } catch (HttpMessageNotReadableException | NullPointerException ex) {
            LOGGER.error("Invalid user role {}", role);
            return false;
        }
    }

    public User checkUpdateUserValidations(User request) {
        LOGGER.info("Received request to update user {}", request);

        if (request.getId() == null || request.getId() <= 0) {
            throw new CustomException(ID_REQUIRED);
        }

        Optional<User> optionalUser = userRepository.findById(request.getId());
        if (optionalUser.isEmpty()) {
            throw new CustomException(USER_NOT_FOUND);
        }
        User user = optionalUser.get();

        if (!StringUtils.hasText(request.getName())) {
            user.setName(optionalUser.get().getName());
        } else {
            user.setName(request.getName());
        }
        if (!StringUtils.hasText(request.getEmail())) {
            user.setEmail(optionalUser.get().getEmail());
        } else {
            user.setEmail(request.getEmail());
        }
        if (!StringUtils.hasText(request.getPassword())) {
            user.setPassword(optionalUser.get().getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (!StringUtils.hasText(request.getPhoneNo())) {
            user.setPhoneNo(optionalUser.get().getPhoneNo());
        } else {
            user.setPhoneNo(request.getPhoneNo());
        }
        if (request.getRole() == null) {
            user.setRole(optionalUser.get().getRole());
        } else {
            user.setRole(request.getRole());
        }
        if (request.getActive() == null) {
            user.setActive(optionalUser.get().getActive());
        } else {
            user.setActive(request.getActive());
        }

        if (request.getDeleted() == null) {
            user.setDeleted(optionalUser.get().getDeleted());
        } else {
            user.setDeleted(request.getDeleted());
        }

        user.setModifiedTime(Instant.now());

        return user;
    }

    public User checkLoginValidation(LoginRequest request) {
        if (!StringUtils.hasText(request.email())) {
            throw new CustomException(EMAIL_REQUIRED);
        }
        if (!StringUtils.hasText(request.password())) {
            throw new CustomException(PASSWORD_REQUIRED);
        }

        Optional<User> user = userRepository.findByEmailAndDeletedFalse(request.email());
        if (user.isPresent()) {

            boolean isMatched = passwordEncoder.matches(request.password(), user.get().getPassword());
            if (!isMatched) {
                throw new CustomException(EMAIL_PASSWORD_MISMATCH);
            }
            return user.get();
        } else {
            throw new CustomException(INVALID_LOGIN_CREDENTIALS);
        }


    }
}
