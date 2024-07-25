package com.atipera.searcher.controller;

import com.atipera.searcher.dto.github.RepositoryDetails;
import com.atipera.searcher.service.GithubSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling GitHub repository search operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/github/search")
public class GithubSearchController {

    private final GithubSearchService githubSearchService;

    /**
     * Retrieves details of all repositories for a specified GitHub username.
     *
     * @param username the GitHub username
     * @return ResponseEntity containing a list of {@link RepositoryDetails}
     */
    @GetMapping(value = "/{username}", produces = "application/json")
    public ResponseEntity<List<RepositoryDetails>> getRepositoriesDetails(@PathVariable String username) {
        return ResponseEntity.ok(githubSearchService.processNonForkedUserRepositories(username));
    }
}
