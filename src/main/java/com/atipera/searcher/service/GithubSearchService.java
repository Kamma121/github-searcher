package com.atipera.searcher.service;

import com.atipera.searcher.client.GithubApiClient;
import com.atipera.searcher.dto.github.BranchDetails;
import com.atipera.searcher.dto.github.BranchResponse;
import com.atipera.searcher.dto.github.RepositoryDetails;
import com.atipera.searcher.dto.github.RepositoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for searching GitHub repositories and processing their details.
 * Utilizes the {@link GithubApiClient} to fetch repository and branch information from GitHub.
 */
@Service
@RequiredArgsConstructor
public class GithubSearchService {

    private final GithubApiClient githubApiClient;

    /**
     * Retrieves and processes all non-forked repositories for a specified GitHub user.
     * This method filters out forked repositories and constructs detailed representations
     * of the remaining repositories, including their names and owners.
     *
     * @param username the GitHub username
     * @return A list of {@link RepositoryDetails} excluding forked repositories
     */
    public List<RepositoryDetails> processNonForkedUserRepositories(String username) {
        var repositoryResponseList = githubApiClient.fetchUserRepositories(username);
        return repositoryResponseList.stream()
                .filter(repository -> !repository.isForked())
                .map(this::createRepositoryDetailsFromResponse)
                .toList();
    }

    /**
     * Retrieves and processes all branches for a given repository of a user.
     * This method fetches branch information and constructs a detailed representation
     * of each branch, including its name and latest commit SHA.
     *
     * @param repositoryName The name of the repository
     * @param username       the GitHub username who owns the repository
     * @return A list of {@link BranchDetails} objects representing each branch in the repository
     */
    private List<BranchDetails> processRepositoryBranches(String repositoryName, String username) {
        var branchResponseList = githubApiClient.fetchRepositoryBranches(repositoryName, username);
        return branchResponseList.stream()
                .map(this::createBranchDetailsFromResponse)
                .toList();
    }

    /**
     * Creates a {@link BranchDetails} instance from a branch response.
     * This method extracts the branch name and latest commit SHA from the response,
     * encapsulating them in a {@link BranchDetails} object.
     *
     * @param branchResponse the branch response to process
     * @return a {@link BranchDetails} containing the branch's name and commit SHA
     */
    private BranchDetails createBranchDetailsFromResponse(BranchResponse branchResponse) {
        return BranchDetails.builder()
                .name(branchResponse.getName())
                .commitSha(branchResponse.getCommit().getSha())
                .build();
    }

    /**
     * Creates a {@link RepositoryDetails} instance from a repository response.
     * This method processes the repository's branches and encapsulates the repository's
     * name, owner's login, and branches in a {@link RepositoryDetails} object.
     *
     * @param repositoryResponse the repository response to process
     * @return a {@link RepositoryDetails} containing the repository's details
     */
    private RepositoryDetails createRepositoryDetailsFromResponse(RepositoryResponse repositoryResponse) {
        return RepositoryDetails.builder()
                .repositoryName(repositoryResponse.getName())
                .ownerLogin(repositoryResponse.getOwner().getLogin())
                .branches(processRepositoryBranches(repositoryResponse.getName(),
                        repositoryResponse.getOwner().getLogin()))
                .build();
    }
}
