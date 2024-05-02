package edu.java.scrapper.controller;

import edu.java.controller.LinksController;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.rateLimiting.RateLimitingService;
import edu.java.service.LinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = LinksController.class, properties = "app.rate-limiting-config.limit=5")
@Import({RateLimitingService.class})
public class LinksControllerTest {
    private static final long TG_CHAT_ID = 1L;
    private static final String TG_CHAT_ID_HEADER = "Tg-Chat-Id";
    private static final String PATH = "/links";
    private static final int LIMIT = 5;
    private static final String URI_LINK = "https://stackoverflow.com/";
    @Autowired
    MockMvc mvc;

    @MockBean
    LinkService linkService;
    @Autowired
    RateLimitingService rateLimitingService;

    @BeforeEach
    void cleatBucket() {
        rateLimitingService.clearBucket();
    }

    @Test
    void testGetLinks_shouldThrowRateLimitingException() throws Exception {
        when(linkService.listAll(1L)).thenReturn(Mockito.mock(ListLinksResponse.class));
        for (int i = 0; i < LIMIT; i++) {
            mvc.perform(get(PATH).header(TG_CHAT_ID_HEADER, TG_CHAT_ID))
               .andExpect(status().isOk());
        }
        mvc.perform(get(PATH).header(TG_CHAT_ID_HEADER, TG_CHAT_ID)).andExpect(status().is(429));
    }

    @Test
    void testAddLink_shouldThrowRateLimitingException() throws Exception {
        when(linkService.add(TG_CHAT_ID, URI.create(URI_LINK))).thenReturn(Mockito.mock(LinkResponse.class));
        String body = "{\"link\": \"" + URI_LINK + "\"}";
        for (int i = 0; i < LIMIT; i++) {
            mvc.perform(post(PATH).header(TG_CHAT_ID_HEADER, TG_CHAT_ID)
                                  .accept(APPLICATION_JSON_VALUE)
                                  .contentType(APPLICATION_JSON_VALUE)
                                  .content(body))
               .andExpect(status().isOk());
        }
        mvc.perform(post(PATH).header(TG_CHAT_ID_HEADER, TG_CHAT_ID).contentType(APPLICATION_JSON_VALUE).content(body))
           .andExpect(status().is(429));
    }

    @Test
    void testDeleteLink_shouldThrowRateLimitingException() throws Exception {
        when(linkService.remove(TG_CHAT_ID, URI.create(URI_LINK))).thenReturn(Mockito.mock(LinkResponse.class));
        String body = "{\"link\": \"" + URI_LINK + "\"}";
        for (int i = 0; i < LIMIT; i++) {
            mvc.perform(delete(PATH).header(TG_CHAT_ID_HEADER, TG_CHAT_ID)
                                    .contentType(APPLICATION_JSON_VALUE)
                                    .content(body))
               .andExpect(status().isOk());
        }
        mvc.perform(delete(PATH).header(TG_CHAT_ID_HEADER, TG_CHAT_ID)
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(body)).andExpect(status().is(429));
    }
}
