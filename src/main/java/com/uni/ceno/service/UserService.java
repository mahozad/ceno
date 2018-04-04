package com.uni.ceno.service;

import com.uni.ceno.model.Role;
import com.uni.ceno.model.User;
import com.uni.ceno.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Primary
@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public boolean checkUsername(String username) {
        return userRepository.existsByName(username);
    }

    public byte[] getAvatarByUsername(String username) {
        return userRepository.getByName(username).getAvatar();
    }

    public boolean addUser(String username, String plainPassword, MultipartFile avatar) {
        if (userRepository.existsByName(username)) {
            return false;
        }
        try {
            username = username.toLowerCase().trim();
            String password = encoder.encode(plainPassword);
            byte[] avatarBytes = avatar.getBytes();
            User user = new User(username, password, avatarBytes, Role.USER);
            userRepository.save(user);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("user <" + username + "> not found");
        }
        return user;
    }
}
