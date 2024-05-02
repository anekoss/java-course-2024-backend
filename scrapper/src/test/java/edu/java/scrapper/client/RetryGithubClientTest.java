package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubBranchResponse;
import edu.java.client.dto.GitHubResponse;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RetryGithubClientTest extends IntegrationTest {

    private static final String THIRD_STATE = "third";
    private static final String SECOND_STATE = "second";
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private static final Path OK_RESPONSE_PATH = Path.of("src/test/java/edu/java/scrapper/client/github/github_ok.json");
    private static final Path BAD_RESPONSE_PATH = Path.of("src/test/java/edu/java/scrapper/client/github/github_bad.json");
    private static final Path OK_BRANCH_RESPONSE_PATH =
            Path.of("src/test/java/edu/java/scrapper/client/github/github_branch_ok.json");
    @Autowired
    private GitHubClient gitHubClient;


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.github.base-url", wireMockServer::baseUrl);
        registry.add("app.retry-config.policy", () -> "constant");
        registry.add("app.retry-config.max-attempts", () -> 3);
    }

    @BeforeEach
    void provideWireMockServerForGetRepositoryTest() throws IOException {
        String response = String.join("", Files.readAllLines(BAD_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(STARTED)
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)).willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)).willSetStateTo(THIRD_STATE));
    }

    @BeforeEach
    void provideWireMockServerForGetRepositoryBranchesTest() throws IOException {
        String response = String.join("", Files.readAllLines(OK_BRANCH_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project/branches")
                                       .inScenario("getRepositoryBranches")
                                       .whenScenarioStateIs(STARTED)
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)).willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project/branches")
                                       .inScenario("getRepositoryBranches")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)).willSetStateTo(THIRD_STATE));
    }


    @Test
    void testGetRepository_shouldReturnCorrectResponse() throws IOException {
        String response = String.join("", Files.readAllLines(OK_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(200)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response))
        );
        GitHubResponse excepted = new GitHubResponse(
                755879115L,
                "tinkoff-project",
                "anekoss/tinkoff-project",
                OffsetDateTime.parse("2024-02-11T11:13:17Z"),
                OffsetDateTime.parse("2024-02-21T12:54:35Z"),
                OffsetDateTime.parse("2024-02-11T11:13:57Z")
        );
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isPresent();
        assertEquals(actual.get(), excepted);
    }

    @Test
    void testGetRepository_shouldReturnEmptyOptionalIfClientError() throws IOException {
        String response = String.join("", Files.readAllLines(BAD_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(404)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)));
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isEmpty();
    }

    @Test
    void testGetRepository_shouldReturnEmptyOptionalIfServerError() throws IOException {
        String response = String.join("", Files.readAllLines(BAD_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)));
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isEmpty();
    }

    @Test
    void testGetRepository_shouldReturnEmptyOptionalIfBadBody() {
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(200)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody("{id:mewmew}")));
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isEmpty();
    }

    @Test
    void testGetRepositoryBranches_shouldReturnOptionalEmptyIfServerError() throws IOException {
        String response = String.join("", Files.readAllLines(OK_BRANCH_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project/branches")
                                       .inScenario("getRepositoryBranches")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)));
        assert gitHubClient.fetchRepositoryBranches("anekoss", "tinkoff-project").isEmpty();
    }

    @Test
    void testGetRepository_shouldReturnOptionalEmptyIfBadResponseBody() {
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .inScenario("getRepository")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(200)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody("{id:mewmew}")));
        assert gitHubClient.fetchRepositoryBranches("anekoss", "tinkoff-project").isEmpty();
    }

    @Test
    void testGetRepositoryBranches_shouldReturnCorrectResponse() throws IOException {
        String response = String.join("", Files.readAllLines(OK_BRANCH_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project/branches")
                                       .inScenario("getRepositoryBranches")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(200)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)));
        GitHubBranchResponse[] excepted =
                new GitHubBranchResponse[]{new GitHubBranchResponse("hw1"), new GitHubBranchResponse("hw2"),
                        new GitHubBranchResponse("hw_2"), new GitHubBranchResponse("hw3")};
        GitHubBranchResponse[] actual = gitHubClient.fetchRepositoryBranches("anekoss", "tinkoff-project").get();
        assert actual.length == 4;
        assertEquals(actual[0], excepted[0]);
        assertEquals(actual[1], excepted[1]);
        assertEquals(actual[2], excepted[2]);
        assertEquals(actual[3], excepted[3]);

    }

    @Test
    void testGetRepositoryBranches_shouldReturnOptionalEmptyIfClientError() throws IOException {
        String response = String.join("", Files.readAllLines(BAD_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project/branches")
                                       .inScenario("getRepositoryBranches")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(aResponse().withStatus(404)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response)));
        assert gitHubClient.fetchRepositoryBranches("anekoss", "tinkoff-project").isEmpty();
    }
}
