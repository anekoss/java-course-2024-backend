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

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RetryLinksClientTest {
    private static final String THIRD_STATE = "third";
    private static final String SECOND_STATE = "second";
    private static final String FOURTH_STATE = "fourth";
    private static final String REQUEST_BODY = "{\"link\":\"https://example.com/link1\"}";
    private static final String RESPONSE = "{\"links\": [{\"id\": 1,\"uri\":\"https://example.com/link1\"}],\"size\": 1}";
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
                                                               .options(wireMockConfig().dynamicPort())
                                                               .build();
    @Autowired
    private LinksClient linksClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.links.base-url", wireMockServer::baseUrl);
        registry.add("app.retry-config.policy", () -> "constant");
        registry.add("app.retry-config.max-attempts", () -> 4);
    }


    static void provideDataForGetLinksTest() {
        wireMockServer.stubFor(WireMock.get(anyUrl())
                                       .inScenario("getLinks")
                                       .whenScenarioStateIs(STARTED)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.get(anyUrl())
                                       .inScenario("getLinks")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(THIRD_STATE));
        wireMockServer.stubFor(WireMock.get(anyUrl())
                                       .inScenario("getLinks")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(FOURTH_STATE));
    }

    static void provideDataForDeleteLinkTest() {
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .inScenario("deleteLink")
                                       .whenScenarioStateIs(STARTED)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(REQUEST_BODY))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .inScenario("deleteLink")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(REQUEST_BODY))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(THIRD_STATE));
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .inScenario("deleteLink")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(REQUEST_BODY))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(FOURTH_STATE));

    }

    static void provideDataForAddLinkTest() {
        wireMockServer.stubFor(WireMock.post(anyUrl())
                                       .inScenario("addLink")
                                       .whenScenarioStateIs(STARTED)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(REQUEST_BODY))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(SECOND_STATE));
        wireMockServer.stubFor(WireMock.post(anyUrl())
                                       .inScenario("addLink")
                                       .whenScenarioStateIs(SECOND_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(REQUEST_BODY))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(THIRD_STATE));
        wireMockServer.stubFor(WireMock.post(anyUrl())
                                       .inScenario("addLink")
                                       .whenScenarioStateIs(THIRD_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(REQUEST_BODY))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500))
                                       .willSetStateTo(FOURTH_STATE));
    }

    @Test
    void testGetLinks_shouldReturnCorrectResponse()
            throws URISyntaxException, CustomClientErrorException, CustomServerErrorException {
        provideDataForGetLinksTest();
        ListLinksResponse excepted =
                new ListLinksResponse(new LinkResponse[]{new LinkResponse(1L, new URI("https://example.com/link1"))}, 1L);
        wireMockServer.stubFor(WireMock.get(anyUrl())
                                       .inScenario("getLinks")
                                       .whenScenarioStateIs(FOURTH_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 200)));
        ListLinksResponse actual = linksClient.getLinks(1L);
        assertThat(actual.linkResponses()).isEqualTo(excepted.linkResponses());
        assertThat(actual.size()).isEqualTo(excepted.size());
    }

    @Test
    void testGetLinks_shouldReturnCustomClientExceptionIfClientError() {
        provideDataForGetLinksTest();
        wireMockServer.stubFor(WireMock.get(anyUrl())
                                       .inScenario("getLinks")
                                       .whenScenarioStateIs(FOURTH_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 404)));
        assertThrows(CustomClientErrorException.class, () -> linksClient.getLinks(1L));
    }

    @Test
    void testGetLinks_shouldReturnCustomServerExceptionIfServerError() {
        provideDataForGetLinksTest();
        wireMockServer.stubFor(WireMock.get(anyUrl())
                                       .inScenario("getLinks")
                                       .whenScenarioStateIs(FOURTH_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .willReturn(WireMock.jsonResponse(RESPONSE, 500)));
        assertThrows(CustomServerErrorException.class, () -> linksClient.getLinks(1L));
    }

    @Test
    void testDeleteLink_shouldReturnCorrectResponse()
            throws URISyntaxException, CustomClientErrorException, CustomServerErrorException {
        provideDataForDeleteLinkTest();
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        LinkResponse excepted = new LinkResponse(1L, new URI("https://example.com/link1"));
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .inScenario("deleteLink")
                                       .whenScenarioStateIs(FOURTH_STATE)
                                       .withHeader("Accept", WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                                       .withHeader("Tg-Chat-Id", WireMock.equalTo(String.valueOf(1L)))
                                       .withRequestBody(WireMock.equalToJson(request))
                                       .willReturn(WireMock.jsonResponse(response, 200)));
        assertThat(linksClient.deleteLink(1L, new RemoveLinkRequest("https://example.com/link1"))).isEqualTo(excepted);
    }

    @Test
    void testDelete_linkShouldReturnCustomClientExceptionIfClientError() {
        provideDataForDeleteLinkTest();
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .inScenario("deleteLink")
                                       .whenScenarioStateIs(FOURTH_STATE)
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
        provideDataForDeleteLinkTest();
        String request = "{\"link\":\"https://example.com/link1\"}";
        String response = "{\"id\":1, \"uri\":\"https://example.com/link1\"}";
        wireMockServer.stubFor(WireMock.delete(WireMock.anyUrl())
                                       .inScenario("deleteLink")
                                       .whenScenarioStateIs(FOURTH_STATE)
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
        provideDataForAddLinkTest();
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
        provideDataForAddLinkTest();
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
        provideDataForAddLinkTest();
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

