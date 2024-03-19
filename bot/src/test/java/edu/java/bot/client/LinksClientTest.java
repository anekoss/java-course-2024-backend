package edu.java.bot.client;


import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest(httpsEnabled = true)
public class LinksClientTest {
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    private final String response = "{\"links\": [{\"id\": 1,\"uri\":\"https://example.com/link1\"}],\"size\": 1}";


    private LinksClient linksClient;

    @BeforeEach
    void init() {
        linksClient = new LinksClient(wireMockServer.baseUrl());
    }

    @Test
    void testGetLinksShouldReturnCorrectResponse() throws URISyntaxException {
        ListLinksResponse excepted = new ListLinksResponse(new LinkResponse[]{new LinkResponse(1L, new URI("https://example.com/link1"))}, 1L);
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 200)));
        ListLinksResponse actualResponse = linksClient.getLinks(1L);
        assertThat(actualResponse.size()).isEqualTo(excepted.size());
        assertThat(actualResponse.linkResponses()).isEqualTo(excepted.linkResponses());
    }

    @Test
    void testGetLinksShouldReturnClientError() {
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 404)));
        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> linksClient.getLinks(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND");
    }

    @Test
    void testGetLinksShouldReturnServerError() {
        wireMockServer.stubFor(WireMock.post(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 500)));
        HttpServerErrorException exception = assertThrows(
                HttpServerErrorException.class,
                () -> linksClient.getLinks(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR");
    }


    @Test
    void testDeleteLinkShouldReturnCorrectResponse() throws URISyntaxException {
        ListLinksResponse excepted = new ListLinksResponse(new LinkResponse[]{new LinkResponse(1L, new URI("https://example.com/link1"))}, 1L);
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 200)));
        ListLinksResponse actualResponse = linksClient.getLinks(1L);
        assertThat(actualResponse.size()).isEqualTo(excepted.size());
        assertThat(actualResponse.linkResponses()).isEqualTo(excepted.linkResponses());
    }

    @Test
    void testDeleteLinkShouldReturnClientError() {
        wireMockServer.stubFor(WireMock.get(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 404)));
        HttpClientErrorException exception = assertThrows(
                HttpClientErrorException.class,
                () -> linksClient.getLinks(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("404 NOT_FOUND");
    }

    @Test
    void testDeleteLinkShouldReturnServerError() {
        wireMockServer.stubFor(WireMock.post(WireMock.anyUrl())
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(response, 500)));
        HttpServerErrorException exception = assertThrows(
                HttpServerErrorException.class,
                () -> linksClient.getLinks(1L)
        );
        assertThat(exception.getMessage()).isEqualTo("500 INTERNAL_SERVER_ERROR");
    }


}
