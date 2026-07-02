package in.rahul.learning.model.entity;

import in.rahul.learning.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_phone", columnList = "phone_no")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "created_time", updatable = false)
    private Instant createdTime;

    @Column(name = "modified_time")
    private Instant modifiedTime;
}
