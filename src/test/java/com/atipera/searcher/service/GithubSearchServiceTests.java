package com.atipera.searcher.service;

import com.atipera.searcher.client.GithubApiClient;
import com.atipera.searcher.dto.github.BranchResponse;
import com.atipera.searcher.dto.github.CommitResponse;
import com.atipera.searcher.dto.github.OwnerResponse;
import com.atipera.searcher.dto.github.RepositoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubSearchServiceTests {

    @Mock
    private GithubApiClient githubApiClient;

    @InjectMocks
    private GithubSearchService githubSearchService;

    @Test
    void testProcessAllUserRepositoriesWithNoRepositories() {
        var username = "username";
        when(githubApiClient.fetchUserRepositories(username)).thenReturn(new ArrayList<>());

        var result = githubSearchService.processNonForkedUserRepositories(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessAllUserRepositoriesWithOnlyForkedRepositories() {
        var username = "username";
        var repositoryResponseList = List.of(
                RepositoryResponse.builder()
                        .name("repo1")
                        .isForked(true)
                        .build(),
                RepositoryResponse.builder()
                        .name("repo2")
                        .isForked(true)
                        .build());

        when(githubApiClient.fetchUserRepositories(username)).thenReturn(repositoryResponseList);

        var result = githubSearchService.processNonForkedUserRepositories(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessAllUserRepositoriesWithMultipleRepositories() {
        var username = "username";
        var ownerResponse = OwnerResponse.builder().login(username).build();
        var repositoryResponse1 = RepositoryResponse.builder()
                .name("repo1")
                .isForked(false)
                .owner(ownerResponse)
                .build();
        var repositoryResponse2 = RepositoryResponse.builder()
                .name("repo2")
                .isForked(true)
                .owner(ownerResponse)
                .build();

        var commitResponse = CommitResponse.builder()
                .sha("123abc")
                .build();
        var branchResponseList = List.of(BranchResponse.builder()
                .name("main")
                .commit(commitResponse)
                .build());


        when(githubApiClient.fetchUserRepositories(username)).thenReturn(List.of(repositoryResponse1, repositoryResponse2));
        when(githubApiClient.fetchRepositoryBranches("repo1", username)).thenReturn(branchResponseList);

        var result = githubSearchService.processNonForkedUserRepositories(username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("repo1", result.get(0).getRepositoryName());
        assertEquals(1, result.get(0).getBranches().size());
        assertEquals("main", result.get(0).getBranches().get(0).getName());
        assertEquals("123abc", result.get(0).getBranches().get(0).getCommitSha());
    }
}
