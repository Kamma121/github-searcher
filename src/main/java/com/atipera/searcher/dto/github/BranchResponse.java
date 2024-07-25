package com.atipera.searcher.dto.github;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the response structure for a branch within a GitHub repository.
 * This class includes the branch's name and latest commit.
 */
@Getter
@Setter
@Builder
public class BranchResponse {
    private String name;
    private CommitResponse commit;
}
