package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
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
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RetryBotClientTest extends IntegrationTest {
    private static final String THIRD_STATE = "third";
    private static final String SECOND_STATE = "second";
    private static final LinkUpdateRequest LINK_UPDATE_REQUEST = new LinkUpdateRequest(1L, "https://api.stackexchange.com", "description", new long[]{1L});
    private static final String REQUEST = "{\"id\":1,\"url\":\"https://api.stackexchange.com\",\"description\":\"description\",\"tgChatIds\":[1]}";
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    @Autowired
    private BotClient botClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.botClient.base-url", wireMockServer::baseUrl);
        registry.add("app.retry-config.policy", () -> "constant");
        registry.add("app.retry-config.max-attempts", () -> 3);
    }


    @BeforeEach
    void provideWireMockServerForLinkUpdatesTest() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(REQUEST))
                                       .inScenario("linkUpdates")
                                       .whenScenarioStateIs(STARTED)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500)
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE
                                                           )).willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(REQUEST))
                                       .inScenario("linkUpdates")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500)
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE
                                                           )).willSetStateTo(THIRD_STATE));
    }

    @Test
    void testLinkUpdates_shouldReturnCorrectResponse() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(REQUEST))
                                       .inScenario("linkUpdates")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse().withStatus(200).withBody("updated")));
        Optional<String> actual = botClient.linkUpdates(LINK_UPDATE_REQUEST);
        assert actual.isPresent();
        assertEquals(actual.get(), "updated");
    }

    @Test
    void testLinkUpdates_shouldReturnEmptyOptionalIfClientError() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(REQUEST))
                                       .inScenario("linkUpdates")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(404)
                                                           .withHeader(
                                                                   "Content-Type",
                                                                   MediaType.APPLICATION_JSON_VALUE
                                                           )));
        Optional<String> actual = botClient.linkUpdates(LINK_UPDATE_REQUEST);
        assert actual.isEmpty();
    }

    @Test
    void testLinkUpdate_shouldReturnEmptyOptionalIfServerError() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(REQUEST))
                                       .inScenario("linkUpdates")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .willReturn(WireMock.aResponse()
                                                           .withStatus(500).withHeader(
                                                       "Content-Type",
                                                       MediaType.APPLICATION_JSON_VALUE
                                               )));
        Optional<String> actual = botClient.linkUpdates(LINK_UPDATE_REQUEST);
        assert actual.isEmpty();
    }


}
