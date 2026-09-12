package in.rahul.learning.service.cache;

import in.rahul.learning.exceptions.CustomException;
import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.response.UserResponse;
import in.rahul.learning.model.response.UserResponseXml;
import in.rahul.learning.repo.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceCache {

    private final IUserRepository userRepository;


    @CacheEvict(cacheNames = {"users:all", "users:byId"}, allEntries = true)
    public void saveUsers(User user) {
        userRepository.save(user);
    }

    @CacheEvict(cacheNames = {"users:all", "users:byId"}, allEntries = true)
    public User updateUsers(User user) {
        return userRepository.save(user);
    }

    @Cacheable(cacheNames = "users:all")
    public List<UserResponse> findAllUserDetails() {
        return userRepository.findAllUsers();
    }

    @Cacheable(cacheNames = "users:byId", key = "#id")
    public UserResponse getUserDetailsById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new CustomException("User not found"));
    }

    /*@CacheEvict(cacheNames = "users", allEntries = true)
    public void saveUsers(User user) {
        userRepository.save(user);
    }

    @CacheEvict(cacheNames = {"user", "users"}, allEntries = true)
    public User updateUsers(User user) {
        return userRepository.save(user);
    }

//    @Cacheable(value = "users", key = "'allUsers'")
//    public List<UserResponse> findAllUsers() {
//        return userRepository.findAllUsers();
//    }

    @Cacheable(cacheNames = "users")
    public List<UserResponse> findAllUserDetails() {
        return userRepository.findAllUsers();
       *//* return userRepository.findAllByDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();*//*
    }

    @Cacheable(cacheNames = "users", key = "#id")
    public UserResponse getUserDetailsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));

        return mapToResponse(user);
    }*/

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

    public UserResponseXml getUserByIdXmlFormat(Long id) {

        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return UserResponseXml.builder()
                    .id(user.get().getId())
                    .name(user.get().getName())
                    .email(user.get().getEmail())
                    .phoneNo(user.get().getPhoneNo())
                    .active(user.get().getActive())
                    .deleted(user.get().getDeleted())
                    .createdTime(user.get().getCreatedTime())
                    .modifiedTime(user.get().getModifiedTime())
                    .build();
        }else{
            throw new CustomException("User not found");
        }
    }
}
