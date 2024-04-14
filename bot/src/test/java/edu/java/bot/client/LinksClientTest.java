package edu.java.bot.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import edu.java.bot.client.exception.CustomClientErrorException;
import edu.java.bot.client.exception.CustomServerErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LinksClientTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private final String response = "{\"links\": [{\"id\": 1,\"uri\":\"https://example.com/link1\"}],\"size\": 1}";

    @Autowired
    private LinksClient linksClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.links.base-url", wireMockServer::baseUrl);
    }


    @Test
    void testGetLinks_shouldReturnCorrectResponse()
            throws URISyntaxException, CustomClientErrorException, CustomServerErrorException {
        ListLinksResponse excepted =
                new ListLinksResponse(new LinkResponse[]{new LinkResponse(1L, new URI("https://example.com/link1"))}, 1L);
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 200)));
        ListLinksResponse actual = linksClient.getLinks(1L);
        assertThat(actual.linkResponses()).isEqualTo(excepted.linkResponses());
        assertThat(actual.size()).isEqualTo(excepted.size());

    }

    @Test
    void testGetLinks_shouldReturnCustomClientExceptionIfClientError() {
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 404)));
        assertThrows(CustomClientErrorException.class, () -> linksClient.getLinks(1L));
    }

    @Test
    void testGetLinks_shouldReturnCustomServerExceptionIfServerError() {
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 500)));
        assertThrows(CustomServerErrorException.class, () -> linksClient.getLinks(1L));
    }

    @Test
    void testDeleteLink_shouldReturnCorrectResponse()
            throws URISyntaxException, CustomClientErrorException, CustomServerErrorException {
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        LinkResponse excepted = new LinkResponse(1L, new URI("https://example.com/link1"));
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 200)));
        assertThat(linksClient.deleteLink(1L, new RemoveLinkRequest("https://example.com/link1"))).isEqualTo(excepted);
    }

    @Test
    void testDelete_linkShouldReturnCustomClientExceptionIfClientError() {
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 404)));
        assertThrows(
                CustomClientErrorException.class,
                () -> linksClient.deleteLink(1L, new RemoveLinkRequest("https://example.com/link1"))
        );
    }

    @Test
    void testDeleteLink_shouldReturnCustomServerExceptionIfServerError() {
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 500)));
        assertThrows(
                CustomServerErrorException.class,
                () -> linksClient.deleteLink(1L, new RemoveLinkRequest("https://example.com/link1"))
        );
    }

    @Test
    void testAddLink_shouldReturnCorrectResponse()
            throws URISyntaxException, CustomClientErrorException, CustomServerErrorException {
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        LinkResponse excepted = new LinkResponse(1L, new URI("https://example.com/link1"));
        wireMockServer.stubFor(WireMock.post(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 200)));
        assertThat(linksClient.addLink(1L, new AddLinkRequest("https://example.com/link1"))).isEqualTo(excepted);
    }

    @Test
    void testAddLink_shouldReturnCustomClientExceptionIfClientError() {
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        wireMockServer.stubFor(WireMock.post(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 404)));
        assertThrows(
                CustomClientErrorException.class,
                () -> linksClient.addLink(1L, new AddLinkRequest("https://example.com/link1"))
        );
    }

    @Test
    void testAddLink_shouldReturnCustomServerExceptionIfServerError() {
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        wireMockServer.stubFor(WireMock.post(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 500)));
        assertThrows(
                CustomServerErrorException.class,
                () -> linksClient.addLink(1L, new AddLinkRequest("https://example.com/link1"))
        );
    }

}