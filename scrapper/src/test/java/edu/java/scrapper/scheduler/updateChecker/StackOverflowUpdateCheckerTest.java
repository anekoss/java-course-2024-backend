package edu.java.scrapper.scheduler.updateChecker;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.updateChecker.StackOverflowUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class StackOverflowUpdateCheckerTest {
    private final StackOverflowClient stackOverflowClient = Mockito.mock(StackOverflowClient.class);
    private final StackOverflowUpdateChecker updateChecker = new StackOverflowUpdateChecker(stackOverflowClient);
    private final StackOverflowResponse response =
        new StackOverflowResponse(List.of(new StackOverflowResponse.StackOverflowItem(78056352L,
            "React Leaflet map not Re-rendering",
            "https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering",
            OffsetDateTime.parse("2024-02-25T14:38:10Z"), OffsetDateTime.parse("2024-02-25T14:38:10Z")
        )));
    private final Link link =
        new Link().setUri(URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"))
            .setLinkType(LinkType.STACKOVERFLOW);

    @Test
    void testCheck_shouldCorrectlyUpdateLink() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.of(response));
        OffsetDateTime checkedAt = OffsetDateTime.parse("2023-02-25T14:38:10Z");
        link.setUpdatedAt(checkedAt).setUpdatedAt(checkedAt);
        Link updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getUpdatedAt()).isEqualTo(response.items().getFirst().updatedAt());
        assertThat(updatedLink.getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheck_shouldUpdateCheckedAtIfNoUpdate() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.of(response));
        OffsetDateTime checkedAt = OffsetDateTime.parse("2024-02-25T14:38:10Z");
        link.setUpdatedAt(checkedAt).setCheckedAt(checkedAt);
        Link updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getUpdatedAt()).isEqualTo(checkedAt);
        assertThat(updatedLink.getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheck_shouldReturnSameLinkIfFetchRepositoryOptionalEmpty() {
        when(stackOverflowClient.fetchQuestion(78056352L)).thenReturn(Optional.empty());
        assertEquals(updateChecker.check(link), link);
    }

}
