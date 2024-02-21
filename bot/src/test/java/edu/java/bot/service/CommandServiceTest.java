package edu.java.bot.service;

import edu.java.bot.commands.CommandExecutionStatus;
import edu.java.bot.db.Link;
import edu.java.bot.db.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommandServiceTest {
    private final UserService userServiceMock = Mockito.mock(UserService.class);
    private final RepositoryService repositoryServiceMock = Mockito.mock(RepositoryService.class);
    private final LinkService linkServiceMock = Mockito.mock(LinkService.class);

    private final String testUrl = "test";
    private final CommandService commandService =
        new CommandService(repositoryServiceMock, userServiceMock, linkServiceMock);

    @Test
    public void testStartByUnregisterUser() {
        when(userServiceMock.createUser(any())).thenReturn(Mockito.mock(User.class));
        when(repositoryServiceMock.saveUser(any(), any())).thenReturn(true);
        assertThat(commandService.start(1L)).isEqualTo(CommandExecutionStatus.SUCCESS);
    }

    @Test
    public void testStartByRegisterUser() {
        when(userServiceMock.createUser(any())).thenReturn(Mockito.mock(User.class));
        when(repositoryServiceMock.saveUser(any(), any())).thenReturn(false);
        assertThat(commandService.start(1L)).isEqualTo(CommandExecutionStatus.USER_ALREADY_REGISTER);
    }

    @Test
    public void testTrackAlreadyTrackLink() {
        when(linkServiceMock.createValidLink(any())).thenReturn(Optional.of(Mockito.mock(Link.class)));
        when(repositoryServiceMock.startTrackLink(any(), any(), any())).thenReturn(false);
        assertThat(commandService.track(
            1L,
            testUrl
        )).isEqualTo(CommandExecutionStatus.LINK_ALREADY_TRACK);
    }

    @Test
    public void testTrackInvalidLink() {
        when(linkServiceMock.createValidLink(any())).thenReturn(Optional.empty());
        assertThat(commandService.track(
            1L,
            testUrl
        )).isEqualTo(CommandExecutionStatus.LINK_INVALID);
    }

    @Test
    public void testTrack() {
        when(linkServiceMock.createValidLink(any())).thenReturn(Optional.of(Mockito.mock(Link.class)));
        when(repositoryServiceMock.startTrackLink(any(), any(), any())).thenReturn(true);
        assertThat(commandService.track(
            1L,
            testUrl
        )).isEqualTo(CommandExecutionStatus.SUCCESS);
    }

    @Test
    public void testUnTrackNotYetTrackLink() {
        when(linkServiceMock.createValidLink(any())).thenReturn(Optional.of(Mockito.mock(Link.class)));
        when(repositoryServiceMock.stopTrackLink(any(), any())).thenReturn(false);
        assertThat(commandService.unTrack(
            1L,
            testUrl
        )).isEqualTo(CommandExecutionStatus.LINK_NOT_TRACK);
    }

    @Test
    public void testUnTrackInvalidLink() {
        when(linkServiceMock.createValidLink(any())).thenReturn(Optional.empty());
        assertThat(commandService.unTrack(
            1L,
            testUrl
        )).isEqualTo(CommandExecutionStatus.LINK_INVALID);
    }

    @Test
    public void testUnTrack() {
        when(linkServiceMock.createValidLink(any())).thenReturn(Optional.of(Mockito.mock(Link.class)));
        when(repositoryServiceMock.stopTrackLink(any(), any())).thenReturn(true);
        assertThat(commandService.unTrack(
            1L,
            testUrl
        )).isEqualTo(CommandExecutionStatus.SUCCESS);
    }

    @Test
    public void testStopUnknownUser() {
        when(repositoryServiceMock.removeUser(any())).thenReturn(false);
        assertThat(commandService.stop(1L)).isEqualTo(false);
    }

    @Test
    public void testStopKnownUser() {
        when(repositoryServiceMock.removeUser(any())).thenReturn(true);
        assertThat(commandService.stop(1L)).isEqualTo(true);
    }

}
