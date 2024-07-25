package com.atipera.searcher.dto.github;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the response structure for a commit within a GitHub repository.
 * This class includes the SHA of the latest commit.
 */
@Getter
@Setter
@Builder
public class CommitResponse {
    private String sha;
}
