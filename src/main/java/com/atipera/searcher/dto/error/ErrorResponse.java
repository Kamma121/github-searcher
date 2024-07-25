package com.atipera.searcher.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an error response for API requests.
 * This class includes the status code and message of an error.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}
