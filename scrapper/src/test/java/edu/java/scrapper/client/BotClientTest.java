package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.CustomWebClientException;
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

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testLinkUpdates_shouldReturnCorrectResponse() throws CustomWebClientException {
        wireMockServer.stubFor(WireMock.post(WireMock.urlPathTemplate("/updates"))
                                       .withHeader("Accept", WireMock.containing(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader(
                                               "Content-Type",
                                               WireMock.containing(MediaType.APPLICATION_JSON_VALUE)
                                       )
                                       .withRequestBody(equalToJson(request))
                                       .willReturn(WireMock.aResponse().withStatus(200)));
        assertThat(botClient.linkUpdates(linkUpdateRequest)).isEqualTo(null);
    }

    @Test
    void testLinkUpdates_shouldReturnWebClientExceptionIfClientError() {
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
        assertThrows(CustomWebClientException.class, () -> botClient.linkUpdates(linkUpdateRequest));
    }

    @Test
    void testLinkUpdate_shouldReturnWebClientExceptionIfServerError() {
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
        assertThrows(CustomWebClientException.class, () -> botClient.linkUpdates(linkUpdateRequest));
    }

}
