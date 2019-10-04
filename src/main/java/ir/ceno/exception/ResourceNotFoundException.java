package ir.ceno.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exception that indicates the resource was not found.
 */
@ResponseStatus(NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

}
