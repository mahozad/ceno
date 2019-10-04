package ir.ceno.controller;

import ir.ceno.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Exception handler that catches and handles any {@link RuntimeException}
 */
@ControllerAdvice
@Controller
@Slf4j
public class ExceptionController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * If any error occurs in the program, it is first delegated here
     * (so the <b>page not found</b> error is also delegated here) and then
     * dispatched to specific {@link ExceptionHandler} method if any available
     * (model attributes and view name may be overwritten by that handler method).
     * <p>
     * Note that the controller must implement the {@link ErrorController}
     * interface for this method to be invoked.
     *
     * @param model optional model to add attributes to
     * @return view name of the error page
     */
    @RequestMapping("/error")
    public String handleError(Model model) {
        return handleNotFoundError(model);
    }

    @ExceptionHandler({ResourceNotFoundException.class, PostNotFoundException.class})
    public String handleNotFoundError(Model model) {
        model.addAttribute("message", "exception.not-found");
        model.addAttribute("code", "404");
        return "error";
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public String handleUsernameError(Exception exception, Model model) {
        if (exception instanceof DuplicateUsernameException) {
            model.addAttribute("message", "exception.duplicate-username");
        } else {
            model.addAttribute("message", "exception.invalid-username");
        }
        model.addAttribute("code", "4xx");
        return "error";
    }

    @ExceptionHandler({ImageAspectRatioException.class, ImageSizeException.class})
    public String handleImageError(Exception exception, Model model) {
        if (exception instanceof ImageAspectRatioException) {
            model.addAttribute("message", "exception.image-aspect-ratio");
        } else {
            model.addAttribute("message", "exception.image-size");
        }
        model.addAttribute("code", "4xx");
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleNotLoggedInError(HttpServletRequest request,
                                         RedirectAttributes redirectAttrs) {
        redirectAttrs.addFlashAttribute("prompt", "exception.not-allowed");
        String referer = request.getHeader("referer");
        return "redirect:" + (referer == null ? "/" : referer);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequests() {
        return "error";
    }

    /*
     * Handle IOException in PostController and RegisterController
     */
    //@ExceptionHandler(Exception.class)
    //public String handleOtherErrors(Exception exception, Model model) {
    //    log.error("Exception occurred in the program", exception);
    //    model.addAttribute("message", "exception.other");
    //    model.addAttribute("code", "500");
    //    return "error";
    //}
}
