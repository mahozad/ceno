package ir.ceno.controller;

import ir.ceno.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    private UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam MultipartFile avatar, HttpServletRequest request,
                           @RequestHeader("referer") String referer,
                           RedirectAttributes redirectAttrs) throws ServletException {
        boolean userAdded = userService.addUser(username, password, avatar);
        if (userAdded) {
            redirectAttrs.addFlashAttribute("prompt", "welcome");
            request.login(username, password);
        }
        return "redirect:" + referer;
    }
}
