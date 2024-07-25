package com.atipera.searcher.dto.github;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Represents the details of a GitHub repository.
 * This model includes the repository's name, the login of the owner, and a list of its branches.
 */
@Getter
@Builder
public class RepositoryDetails {
    private String repositoryName;
    private String ownerLogin;
    private List<BranchDetails> branches;
}
