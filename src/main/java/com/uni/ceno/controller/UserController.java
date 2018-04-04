package com.uni.ceno.controller;

import com.uni.ceno.service.UserService;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        return userService.checkUsername(name);
    }

    @GetMapping("/avatars/{username}")
    @ResponseBody
    public ResponseEntity<byte[]> getAvatar(@PathVariable String username) {
        byte[] avatar = userService.getAvatarByUsername(username);
        MediaType mimeType = MediaType.valueOf(new Tika().detect(avatar));
        return ResponseEntity.ok().contentType(mimeType).body(avatar);
    }
}
