package uz.medsu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.medsu.enums.Gender;
import uz.medsu.entity.Role;
import uz.medsu.enums.Roles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true)
    private String phone;
    private String imageUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Roles profession;
    private Boolean enabled;
    private Boolean isNonLocked;
    private String locale;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toString()));
        for (Authority authority : role.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(authority.getAuthorities().toString()));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
