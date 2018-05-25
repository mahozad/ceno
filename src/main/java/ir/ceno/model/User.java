package ir.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Entity representing a user of the site whether regular or admin.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "name")
public class User implements UserDetails {

    public enum Role {
        USER, ADMIN
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToMany(mappedBy = "likers", fetch = EAGER)
    private Set<Post> favorites = new HashSet<>();

    @OneToMany(mappedBy = "author", fetch = EAGER)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(cascade = ALL, orphanRemoval = true)
    private File avatar;

    @NaturalId
    private String name;

    @Size(min = 6)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private long score;

    public User(String name, String password, File avatar, Role role) {
        this.name = name;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
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
}
