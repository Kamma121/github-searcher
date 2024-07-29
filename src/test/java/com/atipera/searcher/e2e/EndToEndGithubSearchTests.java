package com.atipera.searcher.e2e;

import com.atipera.searcher.dto.error.ErrorResponse;
import com.atipera.searcher.dto.github.RepositoryDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class EndToEndGithubSearchTests {
    @LocalServerPort
    private int port;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testGetRepositoryDetailsReturnsCorrectRepositoryDetails() {
        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"repo1\",\"owner\":{\"login\":\"octocat\"},\"fork\":false}," +
                                "{\"name\":\"repo2\",\"owner\":{\"login\":\"octocat\"},\"fork\":true}]")));

        stubFor(get(urlEqualTo("/repos/octocat/repo1/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"main\",\"commit\":{\"sha\":\"123abcd\"}}," +
                                "{\"name\":\"feature\",\"commit\":{\"sha\":\"456def\"}}]")));

        var result = webTestClient.get()
                .uri("/github/search/octocat")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryDetails.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(1, result.size());
        var repo = result.get(0);
        assertEquals("repo1", repo.getRepositoryName());
        assertEquals("octocat", repo.getOwnerLogin());
        assertEquals(2, repo.getBranches().size());

        var branch1 = repo.getBranches().get(0);
        assertEquals("main", branch1.getName());
        assertEquals("123abcd", branch1.getCommitSha());

        var branch2 = repo.getBranches().get(1);
        assertEquals("feature", branch2.getName());
        assertEquals("456def", branch2.getCommitSha());
    }

    @Test
    void testGetRepositoriesDetailsWithOnlyForkedRepos() {
        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"repo1\",\"owner\":{\"login\":\"octocat\"},\"fork\":true}]")));

        webTestClient.get()
                .uri("/github/search/octocat")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryDetails.class)
                .hasSize(0);
    }

    @Test
    void testGetRepositoriesDetailsThrowsFetchFailedException() {
        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withStatus(500)));

        var result = webTestClient.get()
                .uri("/github/search/octocat")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(500, result.getStatus());
        assertEquals("Unable to fetch data from the GitHub API", result.getMessage());
    }

    @Test
    void testGetRepositoriesDetailsThrowsUserNotFoundException() {
        stubFor(get(urlEqualTo("/users/wrong_username/repos"))
                .willReturn(aResponse()
                        .withStatus(404)));

        var result = webTestClient.get()
                .uri("/github/search/wrong_username")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(404, result.getStatus());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void testGetRepositoriesDetailsThrowsRateLimitExceededException() {
        stubFor(get(urlEqualTo("/users/octocat/repos"))
                .willReturn(aResponse()
                        .withStatus(403)));

        var result = webTestClient.get()
                .uri("/github/search/octocat")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(result);
        assertEquals(403, result.getStatus());
        assertEquals("API rate limit exceeded", result.getMessage());
    }

}
