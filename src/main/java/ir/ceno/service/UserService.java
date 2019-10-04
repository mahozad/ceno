package ir.ceno.service;

import ir.ceno.exception.DuplicateUsernameException;
import ir.ceno.model.FileDetails;
import ir.ceno.model.User;
import ir.ceno.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Primary
@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Value("${users-files-path}")
    private String filesPath;

    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * Adds new user to the site.
     * <p>
     * It isn't necessary to check the password since hibernate
     * validation checks its length when persisting the entity.
     * <p>
     * This method is synchronized so no two user with the same username could be created.
     *
     * @param username the username for the user which must be unique
     * @param password plain password for this user
     * @param avatar   avatar image for this user
     * @throws IOException if execution of avatar.getBytes() is not successful
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) throws IOException {
        // no need of the following because the method is transactional
        /*if (userRepository.existsByUsername(user.getUsername())) {
            // TODO: delegate this to database
            throw new DuplicateUsernameException();
        }*/
        user.setPassword(encoder.encode(user.getPassword()));

        File diskFile = new File(filesPath + user.getUsername());
        user.getAvatar().transferTo(diskFile);

        FileDetails fileDetails = new FileDetails(user.getAvatar());
        user.setFile(fileDetails);

        userRepository.save(user);
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
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Gets the avatar image for the {@link User user}.
     * <p>
     * The image is cached in the <i>avatars</i> cache for a predefined period of time.
     *
     * @param username username of the user to get avatar for
     * @return {@link Optional} containing the avatar {@link File file} if user exists or empty
     * otherwise
     */
    @Cacheable(cacheNames = "avatars")
    public Resource getAvatarByUsername(String username) {
        return new FileSystemResource(filesPath + username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User <" + username + "> not found");
        }
        return user.get();
    }
}
