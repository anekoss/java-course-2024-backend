package edu.java.scrapper.service.updateChecker;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.UpdateType;
import edu.java.repository.StackOverflowLinkRepository;
import edu.java.service.updateChecker.JdbcStackOverflowUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class StackOverflowUpdateCheckerTest {
    private final StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
    private final StackOverflowLinkRepository stackOverflowLinkRepository =
        Mockito.mock(StackOverflowLinkRepository.class);
    private final JdbcStackOverflowUpdateChecker updateChecker =
        new JdbcStackOverflowUpdateChecker(stackOverflowLinkRepository, stackOverflowClient);
    private StackOverflowResponse stackOverflowResponse;
    private Link link;

    @BeforeEach
    void init() throws BadResponseBodyException {
        stackOverflowResponse =
            new StackOverflowResponse(List.of(new StackOverflowResponse.StackOverflowItem(78056352L,
                "React Leaflet map not Re-rendering",
                "https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering",
                2L,
                OffsetDateTime.parse("2024-02-25T14:38:10Z"), OffsetDateTime.parse("2024-02-25T14:38:10Z")
            )));
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(stackOverflowResponse);
        link = new Link(
            URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"),
            LinkType.STACKOVERFLOW
        );
        link.setId(1L);
    }

    @Test
    void testUpdateShouldUpdate() {
        when(stackOverflowLinkRepository.findStackOverflowAnswerCountByLinkId(anyLong())).thenReturn(Optional.of(2L));
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-25T14:38:10Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(UpdateType.UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-25T14:38:10Z"));
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testUpdateShouldUpdateAnswer() {
        when(stackOverflowLinkRepository.findStackOverflowAnswerCountByLinkId(anyLong())).thenReturn(Optional.of(1L));
        when(stackOverflowLinkRepository.update(anyLong(), anyLong())).thenReturn(1);
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-25T14:38:10Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(UpdateType.NEW_ANSWER);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-25T14:38:10Z"));
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testUpdateShouldNotUpdate() throws BadResponseBodyException {
        when(stackOverflowLinkRepository.findStackOverflowAnswerCountByLinkId(anyLong())).thenReturn(Optional.of(2L));
        when(stackOverflowLinkRepository.update(anyLong(), anyLong())).thenReturn(0);
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(stackOverflowResponse);
        OffsetDateTime checkedAt = OffsetDateTime.of(2003, 2, 25, 3, 45, 2, 0, ZoneOffset.UTC);
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(UpdateType.NO_UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(link.getUpdatedAt());
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testUpdateShouldReturnInputLink() throws BadResponseBodyException {
        when(stackOverflowLinkRepository.findStackOverflowAnswerCountByLinkId(anyLong())).thenReturn(Optional.of(2L));
        when(stackOverflowLinkRepository.update(anyLong(), anyLong())).thenReturn(0);
        when(stackOverflowClient.fetchQuestion(78056352L)).thenThrow(BadResponseBodyException.class);
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2024-02-25T14:38:10Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(UpdateType.NO_UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(link.getUpdatedAt());
        assertThat(updatedLink.getKey().getCheckedAt()).isEqualTo(link.getCheckedAt());
    }

}
