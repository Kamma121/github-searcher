package com.atipera.searcher.exception;

/**
 * Custom exception class to indicate a failure during processing operations.
 */
public class ProcessingFailedException extends RuntimeException {
    /**
     * Constructs a new ProcessingFailedException with the specified detail message.
     *
     * @param message the detail message
     */
    public ProcessingFailedException(String message) {
        super(message);
    }
}
