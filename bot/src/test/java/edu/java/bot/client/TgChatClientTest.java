package edu.java.bot.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.bot.client.exception.CustomClientErrorException;
import edu.java.bot.client.exception.CustomServerErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TgChatClientTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    @Autowired
    private TgChatClient tgChatClient;


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.tg-chat.base-url", wireMockServer::baseUrl);
    }


    @Test
    void testRegisterChat_shouldReturnCorrectResponse() throws CustomClientErrorException, CustomServerErrorException {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(200))
        );
        assertThat(tgChatClient.registerChat(1L)).isEqualTo(null);
    }

    @Test
    void testRegisterChat_shouldReturnCustomClientExceptionIfClientError() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(404))
        );
        assertThrows(CustomClientErrorException.class, () -> tgChatClient.registerChat(1L));
    }

    @Test
    void testRegisterChatS_shouldReturnServerError() {
        wireMockServer.stubFor(WireMock.post(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(500)));
        assertThrows(CustomServerErrorException.class, () -> tgChatClient.registerChat(1L));
    }

    @Test
    void testDeleteChat_shouldReturnCorrectResponse() throws CustomClientErrorException, CustomServerErrorException {
        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(200))
        );
        assertThat(tgChatClient.deleteChat(1L)).isEqualTo(null);
    }

    @Test
    void testDeleteChat_shouldReturnCustomClientExceptionIfClientError() {
        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(404))
        );
        assertThrows(CustomClientErrorException.class, () -> tgChatClient.deleteChat(1L));
    }

    @Test
    void testDeleteChat_shouldReturnServerError() {
        wireMockServer.stubFor(WireMock.delete(urlEqualTo("/1"))
                                       .willReturn(aResponse().withStatus(500)));
        assertThrows(CustomServerErrorException.class, () -> tgChatClient.deleteChat(1L));
    }
}