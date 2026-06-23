package in.rahul.learning.security.model;

import in.rahul.learning.model.entity.User;
import in.rahul.learning.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ALL")
@RequiredArgsConstructor
@AllArgsConstructor
public class UserPrinciple implements UserDetails {

    private transient User user;
    private transient Set<UserRole> roles;


    @SuppressWarnings("NullableProblems")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getUserId() {
        return user.getId();
    }
}