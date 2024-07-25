package com.atipera.searcher.client;

import com.atipera.searcher.dto.github.BranchResponse;
import com.atipera.searcher.dto.github.RepositoryResponse;
import com.atipera.searcher.exception.FetchFailedException;
import com.atipera.searcher.exception.ProcessingFailedException;
import com.atipera.searcher.exception.RateLimitExceededException;
import com.atipera.searcher.exception.UserNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Service class for making API calls to GitHub.
 * Utilizes OkHttpClient for HTTP requests and Gson for JSON processing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GithubApiClient {
    private final OkHttpClient okHttpClient;
    private final Gson gson;

    @Value("${github.api.users.url}")
    private String githubUsersApiUrl;

    @Value("${github.api.repos.url}")
    private String githubReposApiUrl;

    /**
     * Fetches all public repositories for a given GitHub username.
     *
     * @param username the GitHub username
     * @return List of {@link RepositoryResponse}
     * @throws ProcessingFailedException if there is an error while processing the request
     */
    public List<RepositoryResponse> fetchUserRepositories(String username) {
        try {
            var request = new Request.Builder()
                    .url(githubUsersApiUrl + username + "/repos")
                    .build();
            var response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                handleErrorResponse(response);
            }
            var type = new TypeToken<List<RepositoryResponse>>() {
            }.getType();
            return gson.fromJson(response.body().string(), type);
        } catch (IOException e) {
            log.error("Failed to process repositories for user {}: {} ", username, e.getMessage());
            throw new ProcessingFailedException("Failed to process repositories information");
        }
    }

    /**
     * Fetches branches for a given repository and username.
     *
     * @param repositoryName the name of the repository
     * @param username       the GitHub username
     * @return List of {@link BranchResponse}
     * @throws ProcessingFailedException if there is an error while processing the request
     */
    public List<BranchResponse> fetchRepositoryBranches(String repositoryName, String username) {
        try {
            var request = new Request.Builder()
                    .url(githubReposApiUrl + username + "/" + repositoryName + "/branches")
                    .build();
            var response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                handleErrorResponse(response);
            }
            var type = new TypeToken<List<BranchResponse>>() {
            }.getType();
            return gson.fromJson(response.body().string(), type);
        } catch (IOException e) {
            log.error("Failed to process branches for repository {}: {}", repositoryName, e.getMessage());
            throw new ProcessingFailedException("Failed to process branches information");
        }
    }

    /**
     * Handles error responses from the GitHub API.
     *
     * @param response the HTTP response to handle
     * @throws UserNotFoundException      if the response code is 404, indicating the user was not found
     * @throws RateLimitExceededException if the response code is 403, indicating the rate limit has been exceeded
     * @throws FetchFailedException       for all other error response codes
     */
    private void handleErrorResponse(Response response) {
        int statusCode = response.code();
        switch (statusCode) {
            case 404:
                throw new UserNotFoundException();
            case 403:
                throw new RateLimitExceededException();
            default:
                throw new FetchFailedException();
        }
    }
}
