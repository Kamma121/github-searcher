package com.atipera.searcher.exception;

import com.atipera.searcher.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Provides global exception handling across the whole application.
 * This class intercepts exceptions thrown by any controller and returns an appropriate HTTP response.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Handles exceptions of type {@link UserNotFoundException}.
     * Constructs a response entity with HTTP status 404 (Not Found) and a custom error message.
     *
     * @param e the caught {@link UserNotFoundException}
     * @return a {@link ResponseEntity} with an {@link ErrorResponse} body and HTTP status 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(Exception e) {
        var errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions of type {@link ProcessingFailedException} and {@link FetchFailedException}.
     * Constructs a response entity with HTTP status 500 (Internal Server Error) and a custom error message.
     *
     * @param e the caught exception, either {@link ProcessingFailedException} or {@link FetchFailedException}
     * @return a {@link ResponseEntity} with an {@link ErrorResponse} body and HTTP status 500
     */
    @ExceptionHandler({ProcessingFailedException.class, FetchFailedException.class})
    public ResponseEntity<ErrorResponse> handleProcessingFailedException(Exception e) {
        var errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions of type {@link RateLimitExceededException}.
     * Constructs a response entity with HTTP status 403 (Forbidden) and a custom error message.
     *
     * @param e the caught {@link RateLimitExceededException}
     * @return a {@link ResponseEntity} with an {@link ErrorResponse} body and HTTP status 403
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(Exception e) {
        var errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

}
