package edu.java.scrapper.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.CustomWebClientException;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.scheduler.LinkUpdaterService;

public class LinkUpdaterSchedulerTest {
    private final BotClient botClient = Mockito.mock(BotClient.class);

    private final LinkUpdaterService linkUpdaterService = Mockito.mock(LinkUpdaterService.class);
    private final LinkUpdaterScheduler linkUpdaterScheduler = new LinkUpdaterScheduler( linkUpdaterService, botClient, 1L);

    private final List<LinkUpdateRequest> response = List.of(
            new LinkUpdateRequest(1L, "https://github.com/anekoss/tinkoff-project", "updates",
                    new long[]{34L, 56L}));
    @Test
    void testUpdate_shouldIgnoreExceptionIfBotClientThrowException() throws CustomWebClientException {
        when(botClient.linkUpdates(any())).thenThrow(CustomWebClientException.class);
        when(linkUpdaterService.getUpdates(1L)).thenReturn(response);
        assertDoesNotThrow(linkUpdaterScheduler::update);
    }
}
