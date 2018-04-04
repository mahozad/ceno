package com.uni.ceno.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToMany(mappedBy = "likers", fetch = EAGER)
    private Set<Post> favorites = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = ALL, fetch = EAGER)
    private Set<Post> posts = new HashSet<>();

    @Lob
    private byte[] avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

    //@NaturalId
    private String name;

    @Size(min = 6)
    private String password;

    @Min(0)
    private long score;

    public User(String name, String password, byte[] avatar, Role role) {
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
