package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.client.exception.CustomServerErrorException;
import edu.java.bot.commands.commandImpl.TrackCommand;
import edu.java.bot.printer.HtmlPrinter;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_LINK_TRACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TrackCommandTest {

    private static final Update updateMock = Mockito.mock(Update.class);
    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command trackCommand = new TrackCommand(commandServiceMock);
    private final Printer printer = new HtmlPrinter();
    private Message message;

    @BeforeEach
    public void init() {
        message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(1L);
        when(updateMock.message()).thenReturn(message);
        when(updateMock.message().text()).thenReturn("/track");

    }

    @Test
    public void testHandle_shouldReturnPromptToEnter() throws CustomServerErrorException {
        assertThat(trackCommand.handle(updateMock, printer)).isEqualTo(
            "Введите URL-ссылку, чтобы отслеживать обновления.");
    }

    @Test
    public void testHandle_shouldReturnFailIfInvalidLink() throws CustomServerErrorException {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.track(any(), any())).thenReturn(CommandExecutionStatus.FAIL_LINK_INVALID);
        assertThat(trackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.FAIL_LINK_INVALID.getMessage());
    }

    @Test
    public void testHandle_shouldReturnSuccessIfLinkNoTrackYet() throws CustomServerErrorException {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.track(any(), any())).thenReturn(SUCCESS_LINK_TRACK);
        assertThat(trackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(SUCCESS_LINK_TRACK.getMessage());
    }

    @Test
    public void testHandle_shouldReturnFailIfLinkAlreadyTrack() throws CustomServerErrorException {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.track(any(), any())).thenReturn(CommandExecutionStatus.FAIL_LINK_ALREADY_TRACK);
        assertThat(trackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.FAIL_LINK_ALREADY_TRACK.getMessage());
    }

    @Test
    public void testHandle_shouldThrowCustomServerExceptionIfServerError() throws CustomServerErrorException {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.track(any(), any())).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> trackCommand.handle(updateMock, printer));
    }
}
