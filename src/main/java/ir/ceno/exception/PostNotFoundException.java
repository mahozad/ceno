package ir.ceno.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exception that indicates the user has not access to the resource.
 */
@ResponseStatus(NOT_FOUND)
public class PostNotFoundException extends RuntimeException {

}
