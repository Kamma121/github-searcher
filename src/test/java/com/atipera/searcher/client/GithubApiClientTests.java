package com.atipera.searcher.client;

import com.atipera.searcher.dto.github.BranchResponse;
import com.atipera.searcher.dto.github.CommitResponse;
import com.atipera.searcher.dto.github.OwnerResponse;
import com.atipera.searcher.dto.github.RepositoryResponse;
import com.atipera.searcher.exception.FetchFailedException;
import com.atipera.searcher.exception.ProcessingFailedException;
import com.atipera.searcher.exception.RateLimitExceededException;
import com.atipera.searcher.exception.UserNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubApiClientTests {
    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private Gson gson;

    @Mock
    private Call call;

    @Mock
    private Response response;

    @Mock
    private ResponseBody responseBody;

    @InjectMocks
    private GithubApiClient githubApiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(githubApiClient, "githubUsersApiUrl", "https://api.github.com/users/");
        ReflectionTestUtils.setField(githubApiClient, "githubReposApiUrl", "https://api.github.com/repos/");
    }

    @Test
    void testFetchUserRepositoriesSuccess() throws IOException {
        var username = "username";

        var owner = new JsonObject();
        owner.addProperty("login", username);

        var repo1 = new JsonObject();
        repo1.addProperty("name", "repo1");
        repo1.addProperty("fork", false);
        repo1.add("owner", owner);
        var repositoryArray = new JsonArray();
        repositoryArray.add(repo1);

        var repositoryResponseList = List.of(RepositoryResponse.builder()
                .name("repo1")
                .isForked(false)
                .owner(OwnerResponse.builder()
                        .login(username)
                        .build())
                .build());
        var type = new TypeToken<List<RepositoryResponse>>() {
        }.getType();

        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(repositoryArray.toString());
        when(gson.fromJson(repositoryArray.toString(), type)).thenReturn(repositoryResponseList);

        var result = githubApiClient.fetchUserRepositories(username);

        assertEquals(repositoryResponseList, result);
        verify(response).isSuccessful();
        verify(okHttpClient).newCall(any(Request.class));
        verify(gson).fromJson(responseBody.string(), type);
    }

    @Test
    void testFetchUserRepositoriesThrowsUserNotFoundException() throws IOException {
        var username = "username";

        when(response.isSuccessful()).thenReturn(false);
        when(response.code()).thenReturn(404);
        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        var thrown = assertThrows(UserNotFoundException.class,
                () -> githubApiClient.fetchUserRepositories(username));


        assertEquals("User not found", thrown.getMessage());
        verify(okHttpClient).newCall(any(Request.class));
    }

    @Test
    void testFetchUserRepositoriesThrowsRateLimitExceededException() throws IOException {
        var username = "username";

        when(response.isSuccessful()).thenReturn(false);
        when(response.code()).thenReturn(403);
        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        var thrown = assertThrows(RateLimitExceededException.class,
                () -> githubApiClient.fetchUserRepositories(username));


        assertEquals("API rate limit exceeded", thrown.getMessage());
        verify(okHttpClient).newCall(any(Request.class));
    }

    @Test
    void testFetchUserRepositoriesThrowsProcessingFailedException() throws IOException {
        var username = "username";

        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException());

        var thrown = assertThrows(ProcessingFailedException.class,
                () -> githubApiClient.fetchUserRepositories(username));

        assertEquals("Failed to process repositories information", thrown.getMessage());
        verify(okHttpClient).newCall(any(Request.class));
    }

    @Test
    void testFetchRepositoryBranchesSuccess() throws IOException {
        var username = "username";
        var repositoryName = "repo1";

        var branch1 = new JsonObject();
        branch1.addProperty("name", "main");
        var commit = new JsonObject();
        commit.addProperty("sha", "1234");
        branch1.add("commit", commit);
        var branchArray = new JsonArray();
        branchArray.add(branch1);

        var branchResponseList = List.of(BranchResponse.builder()
                .name("main")
                .commit(CommitResponse.builder()
                        .sha("1234")
                        .build())
                .build());
        var type = new TypeToken<List<BranchResponse>>() {
        }.getType();

        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(branchArray.toString());
        when(gson.fromJson(responseBody.string(), type)).thenReturn(branchResponseList);


        var result = githubApiClient.fetchRepositoryBranches(repositoryName, username);

        assertEquals(branchResponseList, result);
        verify(response).isSuccessful();
        verify(okHttpClient).newCall(any(Request.class));
        verify(gson).fromJson(responseBody.string(), type);
    }


    @Test
    void testFetchRepositoryBranchesThrowsFetchFailedException() throws IOException {
        var username = "username";
        var repositoryName = "repo1";

        when(response.isSuccessful()).thenReturn(false);
        when(response.code()).thenReturn(500);
        when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(response);

        var thrown = assertThrows(FetchFailedException.class,
                () -> githubApiClient.fetchRepositoryBranches(repositoryName, username));

        assertEquals("Unable to fetch data from the GitHub API", thrown.getMessage());
        verify(okHttpClient).newCall(any(Request.class));
    }

}
