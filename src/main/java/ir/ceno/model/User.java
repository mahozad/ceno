package ir.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.*;

import static ir.ceno.model.User.Role.EDITOR;
import static ir.ceno.model.User.Role.USER;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;
import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;
import static lombok.AccessLevel.NONE;

/**
 * Entity representing a user of the site whether regular or admin.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "username")
public class User implements UserDetails {

    public enum Role {
        USER, EDITOR/*, ADMIN*/
    }

    private static final int EDITOR_SCORE = 20;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "likers", fetch = EAGER)
    private Set<Post> favorites = new HashSet<>();

    @OneToMany(mappedBy = "author", fetch = EAGER)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToOne(cascade = ALL)
    private FileDetails file;

    @NotEmpty
    @NaturalId
    @Pattern(regexp = "\\w{3,50}")
    private String username;

    @Pattern(regexp = "((?=.*\\d)(?=.*\\p{L})).{6,30}")
    private String password;

    @Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", flags = CASE_INSENSITIVE)
    private String email;

    @Pattern(regexp = "[a-zA-Z\\s]{5,30}")
    private String fullName;

    @Past
    @CreationTimestamp
    private LocalDateTime creationDateTime;

    @Enumerated(STRING)
    private Role role = USER;

    @Min(0)
    @Setter(NONE)
    private long score;

    @Transient
    private MultipartFile avatar;

    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    public void incrementScore(long amount) {
        score += amount;
        if (score >= EDITOR_SCORE) {
            role = EDITOR;
        }
    }

    public void decrementScore(long amount) {
        score -= amount;
        if (score < EDITOR_SCORE) {
            role = USER;
        }
    }

    public void addFavorite(Post post) {
        favorites.add(post);
    }

    public void deleteFavorite(Post post) {
        favorites.remove(post);
    }

    public void addPost(Post post) {
        posts.add(post);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
