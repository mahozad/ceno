package ir.ceno.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handle() {
        return "error404";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleInternalError() {
        return "error500";
    }
}
