package ir.ceno.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler that catches any exception thrown by controllers
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFoundError(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleInternalError(RuntimeException e) {
        log.error(e.getMessage());
        return "error";
    }
}
