package com.atipera.searcher.controller;

import com.atipera.searcher.dto.github.BranchDetails;
import com.atipera.searcher.dto.github.RepositoryDetails;
import com.atipera.searcher.exception.FetchFailedException;
import com.atipera.searcher.exception.UserNotFoundException;
import com.atipera.searcher.service.GithubSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubSearchController.class)
class GithubSearchControllerTests {

    private static final String BASE_URL = "/github/search";
    @MockBean
    private GithubSearchService githubSearchService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetRepositoriesDetailsSuccess() throws Exception {
        var username = "testUsername";
        var branchDetails = BranchDetails.builder()
                .name("master")
                .commitSha("123asd")
                .build();
        var repositoryDetails = RepositoryDetails.builder()
                .repositoryName("repo1")
                .ownerLogin(username)
                .branches(List.of(branchDetails))
                .build();


        when(githubSearchService.processNonForkedUserRepositories(username)).thenReturn(List.of(repositoryDetails));

        mockMvc.perform(get(BASE_URL + "/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].repositoryName").value(repositoryDetails.getRepositoryName()))
                .andExpect(jsonPath("$[0].ownerLogin").value(username))
                .andExpect(jsonPath("$[0].branches[0].name").value(branchDetails.getName()))
                .andExpect(jsonPath("$[0].branches[0].commitSha").value(branchDetails.getCommitSha()));
    }

    @Test
    void testGetRepositoriesDetailsThrowsUserNotFoundException() throws Exception {
        var username = "testUsername";

        when(githubSearchService.processNonForkedUserRepositories(username))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(get(BASE_URL + "/{username}", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testGetRepositoriesDetailsThrowsFetchFailedException() throws Exception {
        var username = "testUsername";

        when(githubSearchService.processNonForkedUserRepositories(username))
                .thenThrow(new FetchFailedException());

        mockMvc.perform(get(BASE_URL + "/{username}", username))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unable to fetch data from the GitHub API"));
    }
}
