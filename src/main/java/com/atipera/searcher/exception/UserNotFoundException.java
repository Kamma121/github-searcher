package com.atipera.searcher.exception;

/**
 * Custom exception class to indicate the requested user was not found.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructs a new FetchFailedException with a default error message.
     */
    public UserNotFoundException() {
        super("User not found");
    }
}
