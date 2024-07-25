package com.atipera.searcher.exception;

/**
 * Custom exception class to indicate the API rate limit has been exceeded.
 */
public class RateLimitExceededException extends RuntimeException {
    /**
     * Constructs a new RateLimitExceededException with a default error message.
     */
    public RateLimitExceededException() {
        super("API rate limit exceeded");
    }
}
