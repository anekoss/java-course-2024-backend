package edu.java.bot.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.client.exception.BadResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpServerErrorException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest(httpsEnabled = true)
public class TgChatClientTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private TgChatClient tgChatClient;

    @BeforeEach
    void init() {
        tgChatClient = new TgChatClient(wireMockServer.baseUrl());
    }

    @Test
    void testRegisterChat_shouldReturnCorrectResponse() throws BadResponseException {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(200))
        );
        assertThat(tgChatClient.registerChat(1L)).isEqualTo(null);
    }

    @Test
    void testRegisterChat_shouldReturnBadResponseExceptionIfClientError() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(404))
        );
        BadResponseException exception = assertThrows(
            BadResponseException.class,
            () -> tgChatClient.registerChat(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("Bad response was returned from the service");
    }

    @Test
    void testRegisterChatS_shouldReturnServerError() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(500)));
        HttpServerErrorException exception = assertThrows(
            HttpServerErrorException.class,
            () -> tgChatClient.registerChat(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR");
    }

    @Test
    void testDeleteChat_shouldReturnCorrectResponse() throws BadResponseException {
        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(200))
        );
        assertThat(tgChatClient.deleteChat(1L)).isEqualTo(null);
    }

    @Test
    void testDeleteChat_shouldReturnBadResponseExceptionIfClientError() {
        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(404))
        );
        BadResponseException exception = assertThrows(
            BadResponseException.class,
            () -> tgChatClient.deleteChat(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("Bad response was returned from the service");
    }

    @Test
    void testDeleteChat_shouldReturnServerError() {
        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(500)));
        HttpServerErrorException exception = assertThrows(
            HttpServerErrorException.class,
            () -> tgChatClient.deleteChat(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR");
    }
}
