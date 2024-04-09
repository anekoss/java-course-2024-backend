package edu.java.scrapper.scheduler.stackoverflow.handler;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.stackoverflow.StackOverflowResponseHandler;
import edu.java.scheduler.stackoverflow.handler.StackOverflowQuestionResponseHandler;
import edu.java.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StackOverflowQuestionResponseHandlerTest {
    private final StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
    private final LinkService linkService = Mockito.mock(LinkService.class);
    private final StackOverflowResponseHandler responseHandler =
        new StackOverflowQuestionResponseHandler(stackOverflowClient, linkService);
    private final OffsetDateTime checkedAt = OffsetDateTime.parse("2024-02-25T14:38:10Z");

    private final StackOverflowResponse response =
        new StackOverflowResponse(List.of(new StackOverflowResponse.StackOverflowItem(78056352L,
            "React Leaflet map not Re-rendering",
            "https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering",
            5L,
            OffsetDateTime.parse("2024-02-25T14:38:10Z"), OffsetDateTime.parse("2024-02-25T14:38:10Z")
        )));
    private final LinkEntity link =
        new LinkEntity().setId(1L)
                        .setUri(URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"))
                        .setLinkType(LinkType.STACKOVERFLOW).setCheckedAt(checkedAt).setUpdatedAt(checkedAt);

    @Test
    void testHandle_shouldReturnNoUpdateAndUpdateCheckedAt() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.of(response));
        LinkUpdate actual = responseHandler.handle(78056352L, link);
        assert actual.type() == UpdateType.NO_UPDATE;
        assertEquals(actual.link().getUpdatedAt(), checkedAt);
        assertThat(actual.link().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testHandle_shouldCorrectlyUpdate() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.of(response));
        when(linkService.updateStackOverflowAnswerCount(any())).thenReturn(UpdateType.NO_UPDATE);
        link.setUpdatedAt(link.getUpdatedAt().minusDays(3));
        LinkUpdate actual = responseHandler.handle(78056352L, link);
        assert actual.type() == UpdateType.UPDATE;
        assertThat(actual.link().getUpdatedAt()).isEqualTo(response.items().getFirst().updatedAt());
        assertThat(actual.link().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testHandle_shouldReturnNoUpdateIfClientResponseEmpty() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.empty());
        LinkUpdate actual = responseHandler.handle(78056352L, link);
        assert actual.type() == UpdateType.NO_UPDATE;
        assertEquals(actual.link().getUpdatedAt(), checkedAt);
        assertEquals(actual.link().getCheckedAt(), checkedAt);
    }

    @Test
    void testHandle_shouldCorrectlyUpdateAnswerIfNewAnswer() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.of(response));
        when(linkService.updateStackOverflowAnswerCount(any())).thenReturn(UpdateType.UPDATE_ANSWER);
        link.setUpdatedAt(link.getUpdatedAt().minusDays(2));
        LinkUpdate actual = responseHandler.handle(78056352L, link);
        assert actual.type() == UpdateType.UPDATE_ANSWER;
        assertThat(actual.link().getUpdatedAt()).isEqualTo(response.items().getFirst().updatedAt());
        assertThat(actual.link().getCheckedAt()).isAfter(checkedAt);
    }

}
