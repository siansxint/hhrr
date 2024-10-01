package me.siansxint.hhrr.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.siansxint.hhrr.application.Application;
import me.siansxint.hhrr.employee.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Pattern(regexp = "\\d{3}-\\d{7}-\\d")
    @NotEmpty(message = "You must provide a valid Card ID (000-0000000-0)")
    private @Column(name = "card_id", unique = true, nullable = false) String cardId;
    @NotEmpty
    @Getter
    private @Column(nullable = false) String name;

    @NotEmpty
    @Email
    private @Column(nullable = false) String email;
    @NotEmpty
    private @Column(nullable = false) String password;

    @Enumerated
    @Column(nullable = false)
    private Role role;

    private @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL) List<Application> applications = new ArrayList<>();
    private @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL) Employee employee;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singleton(authority);
    }

    public enum Role {
        USER,
        EMPLOYEE,
        ADMIN
    }
}