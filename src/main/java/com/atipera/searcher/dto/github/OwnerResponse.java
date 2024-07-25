package com.atipera.searcher.dto.github;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the response structure for an owner of a GitHub repository.
 * This class includes the owner's login.
 */
@Getter
@Setter
@Builder
public class OwnerResponse {
    private String login;
}
