package ir.ceno.service;

import ir.ceno.model.File;
import ir.ceno.model.User;
import ir.ceno.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Primary
@Service
@Slf4j
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;
    private Tika fileTypeDetector;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder,
                       Tika fileTypeDetector) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.fileTypeDetector = fileTypeDetector;
    }

    public synchronized boolean usernameExists(String username) {
        return userRepository.existsByName(username);
    }

    public synchronized boolean addUser(String username, String password, MultipartFile avatar) {
        if (userRepository.existsByName(username)) {
            return false;
        }
        try {
            username = username.toLowerCase().trim();
            String encodedPassword = encoder.encode(password);
            byte[] avatarBytes = avatar.getBytes();
            MediaType mediaType = MediaType.valueOf(fileTypeDetector.detect(avatarBytes));
            User user = new User(username, encodedPassword, new File(avatarBytes, mediaType),
                    User.Role.USER);
            userRepository.save(user);
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Cacheable(cacheNames = "avatars")
    public Optional<File> getAvatar(String username) {
        return userRepository.findByName(username).map(User::getAvatar);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByName(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User <" + username + "> not found");
        }
        return user.get();
    }
}
