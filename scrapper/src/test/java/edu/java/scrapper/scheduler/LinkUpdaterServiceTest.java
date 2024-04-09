package edu.java.scrapper.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.LinkUpdaterService;
import edu.java.scheduler.UpdateChecker;
import edu.java.service.LinkService;


public class LinkUpdaterServiceTest {
    private static final LinkService linkService = Mockito.mock(LinkService.class);
    private static final Map<LinkType, UpdateChecker> updateCheckerMap = Mockito.mock(Map.class);
    private static final UpdateChecker updateChecker = Mockito.mock(UpdateChecker.class);

    static {
        when(linkService.update(anyLong(), any(), any())).thenReturn(1L);
        when(updateCheckerMap.get(any())).thenReturn(updateChecker);
    }

    private final LinkUpdaterService linkUpdaterService = new LinkUpdaterService(linkService, updateCheckerMap);

    static Stream<Arguments> provideDataForTest() throws CustomWebClientException {
        OffsetDateTime updated = OffsetDateTime.now();
        List<Link> staleLinks = List.of(
                new Link(
                        1L,
                        URI.create("https://github.com/anekoss/tinkoff-project"),
                        LinkType.GITHUB,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-02-11T11:13:57Z")
                ),
                new Link(
                        2L,
                        URI.create("https://github.com/anekoss/tinkoff"),
                        LinkType.GITHUB,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-03-11T11:13:57Z")
                ),
                new Link(
                        3L,
                        URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"),
                        LinkType.STACKOVERFLOW,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-01-11T11:13:57Z")
                ));
        List<Link> updatedLinks = List.of(
                new Link(
                        1L,
                        URI.create("https://github.com/anekoss/tinkoff-project"),
                        LinkType.GITHUB,
                        updated,
                        updated
                ),
                new Link(
                        2L,
                        URI.create("https://github.com/anekoss/tinkoff"),
                        LinkType.GITHUB,
                        updated,
                        updated
                ),
                new Link(
                        3L,
                        URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"),
                        LinkType.STACKOVERFLOW,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-01-11T11:13:57Z")
                )
        );
        when(linkService.getStaleLinks(3L)).thenReturn(staleLinks);
        when(updateChecker.check(staleLinks.get(0))).thenReturn(updatedLinks.get(0));
        when(updateChecker.check(staleLinks.get(1))).thenReturn(updatedLinks.get(1));
        when(updateChecker.check(staleLinks.get(2))).thenReturn(updatedLinks.get(2));
        return Stream.of(Arguments.of(staleLinks, updatedLinks));
    }

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void testGetUpdates_ShouldCorrectlyReturnUpdates() {
        long[] tgChatIds1 = new long[]{23L, 45L};
        long[] tgChatIds2 = new long[]{24L, 81L};
        when(linkService.getChatIdsByLinkId(1L)).thenReturn(tgChatIds1);
        when(linkService.getChatIdsByLinkId(2L)).thenReturn(tgChatIds2);
        List<LinkUpdateRequest> response = List.of(
                new LinkUpdateRequest(1L, "https://github.com/anekoss/tinkoff-project", "updates",
                        tgChatIds1),
                new LinkUpdateRequest(2L, "https://github.com/anekoss/tinkoff", "updates",
                        tgChatIds2));
        List<LinkUpdateRequest> actual = linkUpdaterService.getUpdates(3L);
        assertEquals(actual, response);
}

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void updateGetUpdates_shouldReturnEmptyListIfNoUpdates(List<Link> staleLinks) throws CustomWebClientException {
        when(updateChecker.check(staleLinks.get(0))).thenReturn(staleLinks.get(0));
        when(updateChecker.check(staleLinks.get(1))).thenReturn(staleLinks.get(1));
        when(updateChecker.check(staleLinks.get(2))).thenReturn(staleLinks.get(2));
        assert linkUpdaterService.getUpdates(3L).isEmpty();
    }

    @Test
    void updateGetUpdates_shouldSkipLinkIfWebClientException() throws CustomWebClientException {
        when(updateChecker.check(any())).thenThrow(CustomWebClientException.class);
        assert linkUpdaterService.getUpdates(3L).isEmpty();
    }
    @Test
    void updateGetUpdates_shouldReturnEmptyIdLimitEq0(){
        when(linkService.getStaleLinks(0L)).thenReturn(List.of());
        assert linkUpdaterService.getUpdates(0L).isEmpty();
    }


}
