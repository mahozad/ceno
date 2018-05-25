package ir.ceno.controller;

import ir.ceno.exception.DuplicateUsernameException;
import ir.ceno.model.User;
import ir.ceno.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller that deals with registration of new {@link User user}s.
 */
@Controller
@Slf4j
public class RegisterController {

    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers the {@link User user} to the site.
     *
     * @param username      the username of the user
     * @param password      plain password of the user
     * @param avatar        the avatar image of the user
     * @param request       the http request object
     * @param referer       the referer header of the request
     * @param redirectAttrs model to add prompt whether user was registered or not
     * @return redirect to the referer page
     */
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           @RequestParam MultipartFile avatar, HttpServletRequest request,
                           @RequestHeader("referer") String referer,
                           RedirectAttributes redirectAttrs) {
        try {
            userService.addUser(username, password, avatar);
            redirectAttrs.addFlashAttribute("prompt", "user.created");
            request.login(username, password);
        } catch (IOException | DuplicateUsernameException | ServletException e) {
            log.error(e.getMessage());
            redirectAttrs.addFlashAttribute("prompt", "user.not-created");
        }
        return "redirect:" + referer;
    }
}
