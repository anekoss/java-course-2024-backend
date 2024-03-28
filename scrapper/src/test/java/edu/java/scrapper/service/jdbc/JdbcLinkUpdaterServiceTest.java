package edu.java.scrapper.service.jdbc;

import edu.java.client.BotClient;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.TgChat;
import edu.java.domain.UpdateType;
import edu.java.repository.LinkRepository;
import edu.java.service.UpdateChecker;
import edu.java.service.jdbc.JdbcLinkUpdaterService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import static edu.java.domain.UpdateType.NO_UPDATE;
import static edu.java.domain.UpdateType.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

public class JdbcLinkUpdaterServiceTest {
    private final LinkRepository linkRepository = Mockito.mock(LinkRepository.class);
    private final BotClient botClient = Mockito.mock(BotClient.class);
    private final Map<LinkType, UpdateChecker> updateCheckerMap = Mockito.mock(Map.class);
    private final UpdateChecker updateChecker = Mockito.mock(UpdateChecker.class);
    private final JdbcLinkUpdaterService linkService =
        new JdbcLinkUpdaterService(linkRepository, updateCheckerMap, botClient, 2L);

    static Stream<Arguments> provideDataForTest() {
        TgChat tgChat = new TgChat(123L);
        OffsetDateTime updated = OffsetDateTime.now();
        return Stream.of(
            Arguments.of(List.of(
                    new Link(
                        1L,
                        URI.create("https://github.com/anekoss/tinkoff-project"),
                        LinkType.GITHUB,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"), Set.of(tgChat)
                    ),
                    new Link(
                        1L,
                        URI.create("https://github.com/anekoss/tinkoff"),
                        LinkType.GITHUB,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-03-11T11:13:57Z"), Set.of(tgChat)
                    ),
                    new Link(
                        1L,
                        URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"),
                        LinkType.STACKOVERFLOW,
                        OffsetDateTime.parse("2023-02-11T11:13:57Z"),
                        OffsetDateTime.parse("2023-01-11T11:13:57Z"), Set.of(tgChat)
                    )
                )
            )
        );

    }

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void updateTestHaveUpdates(
        List<Link> staleLinks
    ) {
        Mockito.when(linkRepository.findStaleLinks(anyLong())).thenReturn(staleLinks);
        Mockito.when(updateCheckerMap.get(any())).thenReturn(updateChecker);
        Mockito.when(updateChecker.check(staleLinks.get(0))).thenReturn(Map.entry(staleLinks.get(0), UPDATE));
        Mockito.when(updateChecker.check(staleLinks.get(1))).thenReturn(Map.entry(staleLinks.get(1), UPDATE));
        Mockito.when(updateChecker.check(staleLinks.get(2))).thenReturn(Map.entry(staleLinks.get(2), NO_UPDATE));
        Mockito.when(linkRepository.update(anyLong(), any(), any())).thenReturn(1);
        Map<Link, UpdateType> actual = linkService.update();
        assertEquals(actual, Map.of(staleLinks.get(0), UPDATE, staleLinks.get(1), UPDATE));
    }

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void updateTestHaveNotUpdates(List<Link> staleLinks) {
        Mockito.when(linkRepository.findStaleLinks(anyLong())).thenReturn(staleLinks);
        Mockito.when(updateCheckerMap.get(any())).thenReturn(updateChecker);
        Mockito.when(updateChecker.check(staleLinks.get(0))).thenReturn(Map.entry(staleLinks.get(0), NO_UPDATE));
        Mockito.when(updateChecker.check(staleLinks.get(1))).thenReturn(Map.entry(staleLinks.get(2), NO_UPDATE));
        Mockito.when(updateChecker.check(staleLinks.get(2))).thenReturn(Map.entry(staleLinks.get(2), NO_UPDATE));
        Mockito.when(linkRepository.update(anyLong(), any(), any())).thenReturn(1);
        Map<Link, UpdateType> actual = linkService.update();
        assertThat(actual.size()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void sendUpdatesHaveUpdates(
        List<Link> staleLinks
    )
        throws BadResponseBodyException {
        Mockito.when(botClient.linkUpdates(any())).thenReturn("ok");
        assertThat(linkService.sendUpdates(Map.of(staleLinks.get(0), UPDATE, staleLinks.get(1), UPDATE))).isEqualTo(2);
    }

    @Test
    void sendUpdatesHaveNotUpdates() {
        assertThat(linkService.sendUpdates(Map.of())).isEqualTo(0);
    }

    @Test
    void sendUpdatesHaveClientException()
        throws BadResponseBodyException {
        Mockito.when(botClient.linkUpdates(any())).thenThrow(BadResponseBodyException.class);
        assertThat(linkService.sendUpdates(Map.of())).isEqualTo(0);
    }
}
