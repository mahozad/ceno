package ir.ceno.controller;

import ir.ceno.exception.ResourceNotFoundException;
import ir.ceno.model.File;
import ir.ceno.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String name) {
        return userService.usernameExists(name);
    }

    @GetMapping("/avatars/{username}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String username) {
        Optional<File> optionalAvatar = userService.getAvatar(username);
        if (optionalAvatar.isPresent()) {
            File avatar = optionalAvatar.get();
            return ResponseEntity.ok().contentType(avatar.getMediaType()).body(avatar.getBytes());
        }
        throw new ResourceNotFoundException();
    }
}
