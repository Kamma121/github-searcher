package com.atipera.searcher.exception;

/**
 * Custom exception class to indicate a failure during fetch operations from the GitHub API.
 */
public class FetchFailedException extends RuntimeException {
    /**
     * Constructs a new FetchFailedException with a default error message.
     */
    public FetchFailedException() {
        super("Unable to fetch data from the GitHub API");
    }
}
