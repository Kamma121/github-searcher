package com.atipera.searcher.dto.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents a branch within a GitHub repository.
 * This class includes the branch's name and the SHA of its latest commit.
 */
@Getter
@Builder
@AllArgsConstructor
public class BranchDetails {
    private String name;
    private String commitSha;
}
