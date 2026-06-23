package in.rahul.learning.service.cache;

import in.rahul.learning.exceptions.CustomException;
import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.response.UserResponse;
import in.rahul.learning.repo.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceCache {

    private final IUserRepository userRepository;

    @CacheEvict(cacheNames = "users", allEntries = true)
    public void saveUsers(User user) {
        userRepository.save(user);
    }

    @CacheEvict(cacheNames = {"user", "users"}, allEntries = true)
    public User updateUsers(User user) {
        return userRepository.save(user);
    }


    @Cacheable(cacheNames = "users")
    public List<UserResponse> findAllUserDetails() {
        return userRepository.findAllUsers();
       /* return userRepository.findAllByDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();*/
    }

    @Cacheable(cacheNames = "users", key = "#id")
    public UserResponse getUserDetailsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));

        return mapToResponse(user);
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
