package edu.java.scrapper.controller;

import edu.java.controller.TgChatController;
import edu.java.rateLimiting.RateLimitingService;
import edu.java.service.TgChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TgChatController.class, properties = "app.rate-limiting-config.limit=5")
@Import({RateLimitingService.class})
public class TgChatControllerTest {
    private static final String PATH = "/tg-chat/1";
    private static final int LIMIT = 5;
    @Autowired
    MockMvc mvc;

    @MockBean
    TgChatService linkService;
    @Autowired
    RateLimitingService rateLimitingService;

    @BeforeEach
    void cleatBucket() {
        rateLimitingService.clearBucket();
    }


    @Test
    void testRegisterChat_shouldThrowRateLimitingException() throws Exception {
        for (int i = 0; i < LIMIT; i++) {
            mvc.perform(post(PATH)).andExpect(status().isOk());
        }
        mvc.perform(post(PATH)).andExpect(status().is(429));
    }

    @Test
    void testUnregisterChat_shouldThrowRateLimitingException() throws Exception {
        for (int i = 0; i < LIMIT; i++) {
            mvc.perform(delete(PATH)).andExpect(status().isOk());
        }
        mvc.perform(delete(PATH)).andExpect(status().is(429));
    }
}
