package ir.ceno.service;

import ir.ceno.exception.DuplicateUsernameException;
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
    private Tika fileTypeDetector;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, Tika fileTypeDetector,
                       BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.fileTypeDetector = fileTypeDetector;
        this.encoder = encoder;
    }

    /**
     * Checks whether username already exists or not.
     * <p>
     * This method takes the same lock as the {@link #addUser(String, String, MultipartFile)}
     * method to ensure while the username is being checked
     * no new user (thus new username) is created.
     *
     * @param username the username to check its existence
     * @return true if username already exists, false otherwise
     */
    public synchronized boolean usernameExists(String username) {
        return userRepository.existsByName(username);
    }

    /**
     * Adds new user to the site.
     * <p>
     * This method is synchronized so no two user with the same username could be created.
     *
     * @param username the username for the user which must be unique
     * @param password plain password for this user
     * @param avatar   avatar image for this user
     * @throws IOException if execution of avatar.getBytes() is not successful
     */
    public synchronized void addUser(String username, String password, MultipartFile avatar)
            throws IOException {
        if (userRepository.existsByName(username)) {
            throw new DuplicateUsernameException();
        }
        username = username.toLowerCase().trim();
        String encodedPassword = encoder.encode(password);
        byte[] avatarBytes = avatar.getBytes();
        MediaType mediaType = MediaType.valueOf(fileTypeDetector.detect(avatarBytes));
        User user = new User(username, encodedPassword,
                new File(avatarBytes, mediaType), User.Role.USER);
        userRepository.save(user);
    }

    /**
     * Gets the avatar image for the {@link User user}.
     * <p>
     * The image is cached in the <i>avatars</i> cache for a predefined period of time.
     *
     * @param username name of the user to get avatar for
     * @return {@link Optional} containing the avatar {@link File file} if user exists or empty
     * otherwise
     */
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
