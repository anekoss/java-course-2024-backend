package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.scrapper.IntegrationTest;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BotClientTest extends IntegrationTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private static LinkUpdateRequest linkUpdateRequest;
    private static String request;
    @Autowired
    private BotClient botClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.botClient.base-url", wireMockServer::baseUrl);
    }

    @BeforeAll
    static void initRequest() {
        linkUpdateRequest = new LinkUpdateRequest(1L, "https://api.stackexchange.com", "description", new long[]{1L});
        request =
                "{\"id\":1,\"url\":\"https://api.stackexchange.com\",\"description\":\"description\",\"tgChatIds\":[1]}";
    }

    @Test
    @AssertTrue
    void testLinkUpdates_shouldReturnCorrectResponse() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlPathTemplate("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(request))
                                       .willReturn(WireMock.aResponse().withStatus(200).withBody("updated")));
        Optional<String> actual = botClient.linkUpdates(linkUpdateRequest);
        assert actual.isPresent();
        assertEquals(actual.get(), "updated");
    }

    @Test
    void testLinkUpdates_shouldReturnEmptyOptionalIfClientError() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlPathTemplate("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(request))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(404)
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE
                                                           )));
        Optional<String> actual = botClient.linkUpdates(linkUpdateRequest);
        assert actual.isEmpty();
    }

    @Test
    void testLinkUpdate_shouldReturnEmptyOptionalIfServerError() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlPathTemplate("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(request))
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500).withHeader(
                                                       "Content-Type",
                                                       MediaType.APPLICATION_JSON_VALUE
                                               )));
        Optional<String> actual = botClient.linkUpdates(linkUpdateRequest);
        assert actual.isEmpty();
    }

}
