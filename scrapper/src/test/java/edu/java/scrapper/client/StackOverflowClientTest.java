package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StackOverflowClientTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private final Path okResponsePath = Path.of("src/test/java/edu/java/scrapper/stackOverflow/stackOverflow_ok.json");
    private final Path badResponsePath =
            Path.of("src/test/java/edu/java/scrapper/stackOverflow/stackOverflow_bad.json");

    @Autowired
    private StackOverflowClient stackOverflowClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.stackOverflow.base-url", wireMockServer::baseUrl);
    }

    @Test
    void testFetchQuestionShouldReturnCorrectResponse() throws IOException {
        String response =
                String.join("", Files.readAllLines(okResponsePath));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)
                                                           .withHeader("Content-Type", "application/json")
                                                           .withBody(response)));
        StackOverflowResponse stackOverflowResponse =
                new StackOverflowResponse(List.of(new StackOverflowResponse.StackOverflowItem(78056352L,
                        "React Leaflet map not Re-rendering",
                        "https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering",
                        OffsetDateTime.parse("2024-02-25T14:38:10Z"), OffsetDateTime.parse("2024-02-25T14:38:10Z")
                )));
        assertThat(stackOverflowClient.fetchQuestion(78056352L)).isEqualTo(stackOverflowResponse);
    }

    @Test
    void testFetchQuestionShouldReturnClientError() throws IOException {
        String response =
                String.join("", Files.readAllLines(badResponsePath));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(404)));
        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> stackOverflowClient.fetchQuestion(78056353L)
        );
        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND");
    }

    @Test
    void testFetchQuestionShouldReturnServerError() throws IOException {
        String response =
                String.join("", Files.readAllLines(badResponsePath));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500)));
        HttpServerErrorException exception = assertThrows(
                HttpServerErrorException.class,
                () -> stackOverflowClient.fetchQuestion(78056352L)
        );
        assertThat(exception.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR");
    }

    @Test
    void testFetchQuestionShouldReturnIllegalException() throws IOException {
        String response =
                String.join("", Files.readAllLines(badResponsePath));
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/2.3/questions/{id}"))
                                       .withPathParam("id", WireMock.equalTo("78056352"))
                                       .withQueryParam("sort", WireMock.equalTo("activity"))
                                       .withQueryParam("site", WireMock.equalTo("stackoverflow"))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(200)
                                                           .withHeader("Content-Type", "application/json")
                                               .withBody("{id:mew}")));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> stackOverflowClient.fetchQuestion(78056352L)
        );
        assertThat(exception.getMessage()).isEqualTo("Bad response body was returned from the service");
    }

}
