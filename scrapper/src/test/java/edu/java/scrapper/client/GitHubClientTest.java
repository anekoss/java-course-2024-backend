package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.scrapper.IntegrationTest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubClientTest extends IntegrationTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private final Path okResponsePath = Path.of("src/test/java/edu/java/scrapper/client/github/github_ok.json");
    private final Path badResponsePath = Path.of("src/test/java/edu/java/scrapper/client/github/github_bad.json");
    @Autowired
    private GitHubClient gitHubClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.github.base-url", wireMockServer::baseUrl);
    }

    @Test
    void testGetRepository_shouldReturnCorrectResponse() throws IOException {
        String response = String.join("", Files.readAllLines(okResponsePath));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .willReturn(aResponse().withStatus(200)
                                                              .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
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
        String response = String.join("", Files.readAllLines(badResponsePath));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .willReturn(aResponse().withStatus(404)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response))
        );
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isEmpty();
    }

    @Test
    void testGetRepository_shouldReturnEmptyOptionalIfServerError() throws IOException {
        String response = String.join("", Files.readAllLines(badResponsePath));
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .willReturn(aResponse().withStatus(500)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody(response))
        );
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isEmpty();
    }

    @Test
    void testGetRepository_shouldReturnEmptyOptionalIfBadBody() {
        wireMockServer.stubFor(WireMock.get("/repos/anekoss/tinkoff-project")
                                       .willReturn(aResponse().withStatus(200)
                                                              .withHeader(
                                                                      "Content-Type",
                                                                      MediaType.APPLICATION_JSON_VALUE
                                                              )
                                                              .withBody("{id:mewmew}")));
        Optional<GitHubResponse> actual = gitHubClient.fetchRepository("anekoss", "tinkoff-project");
        assert actual.isEmpty();
    }

}
