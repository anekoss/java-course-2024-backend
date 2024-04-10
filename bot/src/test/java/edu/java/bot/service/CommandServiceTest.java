package edu.java.bot.service;

import edu.java.bot.client.LinksClient;
import edu.java.bot.client.TgChatClient;
import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import edu.java.bot.client.exception.CustomClientErrorException;
import edu.java.bot.client.exception.CustomServerErrorException;
import edu.java.bot.service.validator.UrlValidator;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_LINK_ALREADY_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_LINK_INVALID;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_LINK_NOT_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_USER_ALREADY_REGISTER;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_LINK_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_LINK_UN_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_REGISTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class CommandServiceTest {
    private final LinksClient linksClient = Mockito.mock(LinksClient.class);
    private final TgChatClient tgChatClient = Mockito.mock(TgChatClient.class);

    private final UrlValidator urlValidator = new UrlValidator();
    private final String testUrl = "https://github.com/anekoss/tinkoff-project";
    private final CommandService commandService = new CommandService(tgChatClient, linksClient, urlValidator);

    private final Long tgChatId = 1L;

    @Test
    public void testStart_shouldReturnSuccessIfUnregisterUser() throws CustomClientErrorException,
        CustomServerErrorException {
        doNothing().when(tgChatClient).registerChat(tgChatId);
        assert commandService.start(1L) == SUCCESS_REGISTER;
    }

    @Test
    public void testStart_shouldReturnFailIfRegisterUser() throws CustomClientErrorException, CustomServerErrorException {
        when(tgChatClient.registerChat(tgChatId)).thenThrow(CustomClientErrorException.class);
        assert commandService.start(1L) == FAIL_USER_ALREADY_REGISTER;
    }

    @Test
    public void testStart_shouldThrowCustomServerExceptionIfServerError()
        throws CustomClientErrorException, CustomServerErrorException {
        when(tgChatClient.registerChat(tgChatId)).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> commandService.start(1L));
    }

    @Test
    public void testStop_shouldReturnTrueIfRegisterUser() throws CustomClientErrorException, CustomServerErrorException {
        doNothing().when(tgChatClient).deleteChat(tgChatId);
        assert commandService.stop(1L);
    }

    @Test
    public void testStop_shouldReturnFalseIfUnRegisterUser() throws CustomClientErrorException, CustomServerErrorException {
        when(tgChatClient.deleteChat(tgChatId)).thenThrow(CustomClientErrorException.class);
        assert !commandService.stop(1L);
    }

    @Test
    public void testStop_shouldThrowCustomServerExceptionIfServerError()
        throws CustomClientErrorException, CustomServerErrorException {
        when(tgChatClient.deleteChat(tgChatId)).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> commandService.stop(1L));
    }

    @Test
    public void testTrack_shouldReturnSuccessIfNoTrackLink() throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.addLink(tgChatId, new AddLinkRequest(testUrl))).thenReturn(new LinkResponse(
            1L,
            URI.create(testUrl)
        ));
        assert commandService.track(tgChatId, testUrl) == SUCCESS_LINK_TRACK;
    }

    @Test
    public void testTrack_shouldReturnFailIfAlreadyTrackLink() throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.addLink(tgChatId, new AddLinkRequest(testUrl))).thenThrow(CustomClientErrorException.class);
        assert commandService.track(tgChatId, testUrl) == FAIL_LINK_ALREADY_TRACK;
    }

    @Test
    public void testTrack_shouldThrowCustomServerExceptionIfServerError()
        throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.addLink(tgChatId, new AddLinkRequest(testUrl))).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> commandService.track(tgChatId, testUrl));
    }

    @Test
    public void testTrack_shouldReturnFailIfInvalidLink() throws CustomServerErrorException {
        assert commandService.track(tgChatId, "testUrl") == FAIL_LINK_INVALID;
    }

    @Test
    public void testUnTrack_shouldReturnSuccessIfTrackLink() throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.deleteLink(tgChatId, new RemoveLinkRequest(testUrl))).thenReturn(new LinkResponse(
            1L,
            URI.create(testUrl)
        ));
        assert commandService.unTrack(tgChatId, testUrl) == SUCCESS_LINK_UN_TRACK;
    }

    @Test
    public void testUnTrack_shouldReturnFailIfNoTrackLink() throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.deleteLink(tgChatId, new RemoveLinkRequest(testUrl))).thenThrow(CustomClientErrorException.class);
        assert commandService.unTrack(tgChatId, testUrl) == FAIL_LINK_NOT_TRACK;
    }

    @Test
    public void testUnTrack_shouldThrowCustomServerExceptionIfServerError()
        throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.deleteLink(
            tgChatId,
            new RemoveLinkRequest(testUrl)
        )).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> commandService.unTrack(tgChatId, testUrl));
    }

    @Test
    public void testUnTrack_shouldReturnFailIfInvalidLink() throws CustomServerErrorException {
        assert commandService.unTrack(tgChatId, "testUrl") == FAIL_LINK_INVALID;
    }

    @Test
    public void testList_shouldReturnEmptySetIfClientException() throws CustomClientErrorException,
        CustomServerErrorException {
        when(linksClient.getLinks(tgChatId)).thenThrow(CustomClientErrorException.class);
        assert commandService.list(tgChatId).isEmpty();
    }

    @Test
    public void testList_shouldThrowCustomServerExceptionIfServerError()
        throws CustomClientErrorException, CustomServerErrorException {
        when(linksClient.getLinks(tgChatId)).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> commandService.list(tgChatId).isEmpty());
    }

    @Test
    public void testList_shouldCorrectlyReturnUriSet() throws CustomClientErrorException, CustomServerErrorException {
        ListLinksResponse response =
            new ListLinksResponse(new LinkResponse[] {new LinkResponse(1L, URI.create(testUrl)),
                new LinkResponse(2L, URI.create("testUrl"))}, 2L);
        when(linksClient.getLinks(tgChatId)).thenReturn(response);
        Set<URI> actual = commandService.list(tgChatId);
        assert actual.size() == 2;
        assertThat(commandService.list(tgChatId)).contains(URI.create("testUrl"), URI.create(testUrl));
    }

}
