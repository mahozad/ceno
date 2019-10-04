package ir.ceno.controller;

import ir.ceno.exception.ResourceNotFoundException;
import ir.ceno.model.User;
import ir.ceno.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * Controller that deals with user-related operations.
 * <p>
 * Requests to urls starting with <i>/users</i> are dispatched here.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Checks whether username already exists or not.
     *
     * @param name the username to check its existence
     * @return true if username already exists, false otherwise
     */
    @PostMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String name) {
        return userService.usernameExists(name);
    }

    /**
     * Returns the avatar image for the {@link User user}.
     *
     * @param username the name of the user to get his/her avatar
     * @return {@link ResponseEntity} containing the image bytes
     * @throws ResourceNotFoundException if the user does not exist
     */
    @GetMapping("/avatars/{username}")
    @ResponseBody
    public ResponseEntity<Resource> getAvatar(@PathVariable String username) {
        Resource resource = userService.getAvatarByUsername(username);
        CacheControl cacheControl = CacheControl.maxAge(1, DAYS).mustRevalidate();
        MediaType mediaType = MediaType.valueOf("image/jpeg");
        return ResponseEntity.ok().cacheControl(cacheControl).contentType(mediaType).body(resource);
    }
}
