package in.rahul.learning.repo;

import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT u from User u where u.deleted = false order by u.createdTime desc")
    List<User> findAllByDeletedFalse();

    @Query("SELECT u from User u where u.email=?1 and u.active = true and u.deleted = false")
    Optional<User> findByEmailAndDeletedFalse(String email);

    //DTO Projection (BEST PRACTICE in real apps)
    @Query("SELECT new in.rahul.learning.model.response.UserResponse(u.id, u.name,u.email,u.role,u.phoneNo,u.active,u.deleted,u.createdTime,u.modifiedTime) FROM User u order by u.createdTime desc")
    List<UserResponse> findAllUsers();
//    @Query("SELECT u FROM User u JOIN FETCH u.orders")
//    List<User> findAllUsersWithOrders();
}
