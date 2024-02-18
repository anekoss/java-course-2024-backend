package edu.java.bot.service;

import edu.java.bot.db.Link;
import edu.java.bot.db.LinkRepository;
import edu.java.bot.db.User;
import edu.java.bot.db.UserRepository;
import edu.java.bot.service.RepositoryService;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class RepositoryServiceTest {
    private final LinkRepository linkRepositoryMock = Mockito.mock(LinkRepository.class);
    private final UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

    private final RepositoryService repositoryService = new RepositoryService(linkRepositoryMock, userRepositoryMock);

    @Test
    public void testSaveUnregisteredUser() {
        when(userRepositoryMock.addUser(eq(1L), any())).thenReturn(Optional.of(1L));
        assertThat(repositoryService.saveUser(1L, Mockito.mock(User.class))).isEqualTo(true);
    }

    @Test
    public void testSaveRegisteredUser() {
        when(userRepositoryMock.addUser(eq(1L), any())).thenReturn(Optional.empty());
        assertThat(repositoryService.saveUser(1L, Mockito.mock(User.class))).isEqualTo(false);
    }

    @Test
    public void testRemoveUser() {
        when(userRepositoryMock.removeUser(any())).thenReturn(Optional.empty());
        assertThat(repositoryService.removeUser(1L)).isEqualTo(false);
    }

    @Test
    public void testStartTrackLinkByUnKnownUser() {
        when(userRepositoryMock.addLinkToUser(eq(1L), any())).thenReturn(Optional.of(Mockito.mock(URL.class)));
        doNothing().when(linkRepositoryMock).addUserToLink(any(), any(), any());
        assertThat(repositoryService.startTrackLink(
            1L,
            Mockito.mock(URL.class),
            Mockito.mock(Link.class)
        )).isEqualTo(true);
    }

    @Test
    public void testStartTrackLinkByKnownUser() {
        when(userRepositoryMock.addLinkToUser(eq(1L), any())).thenReturn(Optional.empty());
        doNothing().when(linkRepositoryMock).addUserToLink(any(), any(), any());
        assertThat(repositoryService.startTrackLink(
            1L,
            Mockito.mock(URL.class),
            Mockito.mock(Link.class)
        )).isEqualTo(false);
    }

    @Test
    public void testStopTrackLinkByKnownUser() {
        when(userRepositoryMock.removeLinkFromUser(eq(1L), any())).thenReturn(Optional.of(Mockito.mock(URL.class)));
        doNothing().when(linkRepositoryMock).removeUserFromLink(anyLong(), (URL) any());
        assertThat(repositoryService.stopTrackLink(
            1L,
            Mockito.mock(URL.class)
        )).isEqualTo(true);
    }

    @Test
    public void testStopTrackLinkByUnKnownUser() {
        when(userRepositoryMock.removeLinkFromUser(eq(1L), any())).thenReturn(Optional.empty());
        doNothing().when(linkRepositoryMock).removeUserFromLink(anyLong(), (URL) any());
        assertThat(repositoryService.startTrackLink(
            1L,
            Mockito.mock(URL.class),
            Mockito.mock(Link.class)
        )).isEqualTo(false);
    }

    @Test
    public void testGetTrackLinksByUnTrackUser() {
        when(userRepositoryMock.getUserLinks(any())).thenReturn(Optional.empty());
        assertThat(repositoryService.getTrackLinks(1L)).isEqualTo(Set.of());
    }

    @Test
    public void testGetTrackLinksByTrackUser() {
        URL urlMock = Mockito.mock(URL.class);
        when(userRepositoryMock.getUserLinks(any())).thenReturn(Optional.of(Set.of(urlMock)));
        assertThat(repositoryService.getTrackLinks(1L)).isEqualTo(Set.of(urlMock));
    }

}
