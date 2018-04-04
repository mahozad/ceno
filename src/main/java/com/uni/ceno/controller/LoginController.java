package com.uni.ceno.controller;

import com.uni.ceno.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LoginController {

    private UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestParam String username, @RequestParam String password,
                         @RequestParam MultipartFile avatar, HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        boolean successful = userService.addUser(username, password, avatar);
        if (successful) {
            request.login(username, password);
        }
        response.sendRedirect(request.getHeader("referer"));
    }
}
