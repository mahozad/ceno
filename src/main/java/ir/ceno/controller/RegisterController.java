package ir.ceno.controller;

import ir.ceno.model.User;
import ir.ceno.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Controller that deals with registration of new {@link User users}.
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
     * <p>
     * Note that in order to prevent Spring from throwing {@link BindException} before entering the
     * method, the {@link BindingResult} parameter should be immediately after the command bean
     * so that Spring knows where to put any validation errors.
     *
     * @param user          the user to be registered
     * @param bindingResult object containing information relating to binding form fields
     * @param request       the http request object
     * @param referer       the referer header of the request
     * @param redirectAttrs model to add prompt whether user was registered or not
     * @return redirect to the referer page
     * @throws IOException      if getting bytes from the avatar was unsuccessful
     * @throws ServletException if login failed
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult bindingResult,
                           HttpServletRequest request, @RequestHeader("referer") String referer,
                           RedirectAttributes redirectAttrs) throws IOException, ServletException {
        if (bindingResult.hasErrors()) {
            return "redirect:/"; // TODO: correct this to show the errors
        }
        String plainPassword = user.getPassword();
        userService.addUser(user);
        redirectAttrs.addFlashAttribute("prompt", "user.created");
        request.login(user.getUsername(), plainPassword);
        return "redirect:" + (referer == null ? "/" : referer);
    }
}
