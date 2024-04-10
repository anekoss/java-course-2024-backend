package edu.java.bot.service;

import edu.java.bot.client.LinksClient;
import edu.java.bot.client.TgChatClient;
import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import edu.java.bot.client.exception.BadResponseException;
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
    public void testStart_shouldReturnSuccessIfUnregisterUser() throws BadResponseException {
        doNothing().when(tgChatClient).registerChat(tgChatId);
        assert commandService.start(1L) == SUCCESS_REGISTER;
    }

    @Test
    public void testStart_shouldReturnFailIfRegisterUser() throws BadResponseException {
        when(tgChatClient.registerChat(tgChatId)).thenThrow(BadResponseException.class);
        assert commandService.start(1L) == FAIL_USER_ALREADY_REGISTER;
    }

    @Test
    public void testStop_shouldReturnTrueIfRegisterUser() throws BadResponseException {
        doNothing().when(tgChatClient).deleteChat(tgChatId);
        assert commandService.stop(1L);
    }

    @Test
    public void testStop_shouldReturnFalseIfUnRegisterUser() throws BadResponseException {
        when(tgChatClient.deleteChat(tgChatId)).thenThrow(BadResponseException.class);
        assert !commandService.stop(1L);
    }

    @Test
    public void testTrack_shouldReturnSuccessIfNoTrackLink() throws BadResponseException {
        when(linksClient.addLink(tgChatId, new AddLinkRequest(testUrl))).thenReturn(new LinkResponse(
            1L,
            URI.create(testUrl)
        ));
        assert commandService.track(tgChatId, testUrl) == SUCCESS_LINK_TRACK;
    }

    @Test
    public void testTrack_shouldReturnFailIfAlreadyTrackLink() throws BadResponseException {
        when(linksClient.addLink(tgChatId, new AddLinkRequest(testUrl))).thenThrow(BadResponseException.class);
        assert commandService.track(tgChatId, testUrl) == FAIL_LINK_ALREADY_TRACK;
    }

    @Test
    public void testTrack_shouldReturnFailIfInvalidLink() {
        assert commandService.track(tgChatId, "testUrl") == FAIL_LINK_INVALID;
    }

    @Test
    public void testUnTrack_shouldReturnSuccessIfTrackLink() throws BadResponseException {
        when(linksClient.deleteLink(tgChatId, new RemoveLinkRequest(testUrl))).thenReturn(new LinkResponse(
            1L,
            URI.create(testUrl)
        ));
        assert commandService.unTrack(tgChatId, testUrl) == SUCCESS_LINK_UN_TRACK;
    }

    @Test
    public void testUnTrack_shouldReturnFailIfNoTrackLink() throws BadResponseException {
        when(linksClient.deleteLink(tgChatId, new RemoveLinkRequest(testUrl))).thenThrow(BadResponseException.class);
        assert commandService.unTrack(tgChatId, testUrl) == FAIL_LINK_NOT_TRACK;
    }

    @Test
    public void testUnTrack_shouldReturnFailIfInvalidLink() {
        assert commandService.unTrack(tgChatId, "testUrl") == FAIL_LINK_INVALID;
    }

    @Test
    public void testList_shouldReturnEmptySetIfClientException() throws BadResponseException {
        when(linksClient.getLinks(tgChatId)).thenThrow(BadResponseException.class);
        assert commandService.list(tgChatId).isEmpty();
    }

    @Test
    public void testList_shouldCorrectlyReturnUriSet() throws BadResponseException {
        ListLinksResponse response =
            new ListLinksResponse(new LinkResponse[] {new LinkResponse(1L, URI.create(testUrl)),
                new LinkResponse(2L, URI.create("testUrl"))}, 2L);
        when(linksClient.getLinks(tgChatId)).thenReturn(response);
        Set<URI> actual = commandService.list(tgChatId);
        assert actual.size() == 2;
        assertThat(commandService.list(tgChatId)).contains(URI.create("testUrl"), URI.create(testUrl));
    }

}
