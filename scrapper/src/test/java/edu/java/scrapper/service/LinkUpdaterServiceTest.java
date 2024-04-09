package edu.java.scrapper.service;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.scheduler.UpdateChecker;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.service.LinkService;
import edu.java.service.LinkUpdaterService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.scheduler.dto.UpdateType.NO_UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class LinkUpdaterServiceTest {
    private static final LinkService linkService = Mockito.mock(LinkService.class);
    private static final Map<LinkType, UpdateChecker> updateCheckerMap = Mockito.mock(Map.class);
    private static final UpdateChecker updateChecker = Mockito.mock(UpdateChecker.class);
    private static final List<LinkEntity> staleLinks =
        List.of(
            new LinkEntity().setId(1L).setUri(URI.create("https://github.com/anekoss/tinkoff")),
            new LinkEntity().setId(2L).setUri(URI.create("https://github.com/anekoss/tinkoff-project")),
            new LinkEntity().setId(3L).setUri(URI.create(
                "https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"))
        );

    static {
        when(linkService.update(anyLong(), any(), any())).thenReturn(1L);
        when(updateCheckerMap.get(any())).thenReturn(updateChecker);
        when(linkService.getStaleLinks(3L)).thenReturn(staleLinks);
        when(updateChecker.check(staleLinks.getFirst())).thenReturn(new LinkUpdate(
            staleLinks.getFirst(),
            UpdateType.UPDATE
        ));
        when(updateChecker.check(staleLinks.get(1))).thenReturn(new LinkUpdate(staleLinks.get(1), UpdateType.UPDATE));
        when(updateChecker.check(staleLinks.getLast())).thenReturn(new LinkUpdate(
            staleLinks.getLast(),
            UpdateType.UPDATE
        ));
    }

    private final LinkUpdaterService linkUpdaterService = new LinkUpdaterService(linkService, updateCheckerMap);

    @Test
    void testGetUpdates_shouldCorrectlyReturnAllLinksIfAllHaveUpdate() {
        when(updateChecker.check(staleLinks.getFirst())).thenReturn(new LinkUpdate(
            staleLinks.getFirst(),
            UpdateType.UPDATE
        ));
        when(linkService.getChatIdsByLinkId(anyLong())).thenReturn(new long[] {1L});
        List<LinkUpdateRequest> actual = linkUpdaterService.getUpdates(3L);
        assert actual.size() == 3;
        assertEquals(actual.getFirst().id(), staleLinks.getFirst().getId());
        assert actual.getFirst().tgChatIds().length == 1;
        assertEquals(actual.getFirst().description(), UpdateType.UPDATE.getMessage());
    }

    @Test
    void testGetUpdates_shouldCorrectlyReturnLinksIfHaveUpdate() {
        when(updateChecker.check(staleLinks.getFirst())).thenReturn(new LinkUpdate(staleLinks.getFirst(), NO_UPDATE));
        when(linkService.getChatIdsByLinkId(anyLong())).thenReturn(new long[] {1L});
        List<LinkUpdateRequest> actual = linkUpdaterService.getUpdates(3L);
        assert actual.size() == 2;
        assertEquals(actual.getFirst().id(), staleLinks.get(1).getId());
    }

    @Test
    void testGetUpdates_shouldNoReturnLinkIfHaveNotTgChatIds() {
        when(linkService.getChatIdsByLinkId(anyLong())).thenReturn(new long[] {});
        List<LinkUpdateRequest> actual = linkUpdaterService.getUpdates(3L);
        assert actual.isEmpty();
    }
}
