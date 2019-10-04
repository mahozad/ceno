package ir.ceno.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * Exception that indicates the provided username has already been used.
 */
@ResponseStatus(CONFLICT)
public class DuplicateUsernameException extends RuntimeException {

}
