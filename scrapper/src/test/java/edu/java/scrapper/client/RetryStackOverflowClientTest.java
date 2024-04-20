package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RetryStackOverflowClientTest extends IntegrationTest {
    private static final String THIRD_STATE = "third";
    private static final String SECOND_STATE = "second";
    private static final Path OK_RESPONSE_PATH =
            Path.of("src/test/java/edu/java/scrapper/client/stackOverflow/stackOverflow_ok.json");
    private static final Path BAD_RESPONSE_PATH =
            Path.of("src/test/java/edu/java/scrapper/client/stackOverflow/stackOverflow_bad.json");
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    @Autowired
    private StackOverflowClient stackOverflowClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.stackOverflow.base-url", wireMockServer::baseUrl);
        registry.add("app.retry-config.policy", () -> "linear");
        registry.add("app.retry-config.max-attempts", () -> 3);
    }

    @BeforeEach
    void provideWireMockServerForFetchQuestionTest() throws IOException {
        String response =
                String.join("", Files.readAllLines(BAD_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .inScenario("fetchQuestion")
                                       .whenScenarioStateIs(STARTED)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody(response))
                                       .willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .inScenario("fetchQuestion")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody(response))
                                       .willSetStateTo(THIRD_STATE));
    }

    @Test
    void testFetchQuestion_shouldReturnCorrectResponse() throws IOException {
        String response =
                String.join("", Files.readAllLines(OK_RESPONSE_PATH));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .inScenario("fetchQuestion")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody(response)));
        StackOverflowResponse stackOverflowResponse =
                new StackOverflowResponse(List.of(new StackOverflowResponse.StackOverflowItem(78056352L,
                        "React Leaflet map not Re-rendering",
                        "https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering",
                        5L,
                        OffsetDateTime.parse("2024-02-25T14:38:10Z"), OffsetDateTime.parse("2024-02-25T14:38:10Z")
                )));
        Optional<StackOverflowResponse> actual = stackOverflowClient.fetchQuestion(78056352L);
        assert actual.isPresent();
        assertEquals(actual.get(), stackOverflowResponse);
    }

    @Test
    void testFetchQuestion_shouldReturnEmptyOptionalIfClientError() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .inScenario("fetchQuestion")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse().withStatus(404)));
        Optional<StackOverflowResponse> actual = stackOverflowClient.fetchQuestion(78056352L);
        assert actual.isEmpty();
    }

    @Test
    void testFetchQuestionShouldReturnEmptyOptionalIfServerError() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .inScenario("fetchQuestion")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse().withStatus(500)));
        Optional<StackOverflowResponse> actual = stackOverflowClient.fetchQuestion(78056352L);
        assert actual.isEmpty();
    }

    @Test
    void testFetchQuestion_shouldReturnEmptyOptionalIfBadBody() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .inScenario("fetchQuestion")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody("{id:mew}")));
        Optional<StackOverflowResponse> actual = stackOverflowClient.fetchQuestion(78056352L);
        assert actual.isEmpty();
    }


}
