package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.commands.commandImpl.UnTrackCommand;
import edu.java.bot.printer.HtmlPrinter;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_LINK_UN_TRACK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UnTrackCommandTest {

    private static final Update updateMock = Mockito.mock(Update.class);
    private static Message message;
    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command unTrackCommand = new UnTrackCommand(commandServiceMock);
    private final Printer printer = new HtmlPrinter();

    @BeforeEach
    public void init() {
        message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(1L);
        when(updateMock.message()).thenReturn(message);
    }

    @Test
    public void testHandle_shouldReturnPromptToEnter() {
        when(message.text()).thenReturn("/untrack");
        assertThat(unTrackCommand.handle(updateMock, printer)).isEqualTo(
            "Введите URL-ссылку, чтобы прекратить отслеживать обновления.");
    }

    @Test
    public void testHandle_shouldReturnFailIfLinkInvalid() {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.FAIL_LINK_INVALID);
        assertThat(unTrackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.FAIL_LINK_INVALID.getMessage());
    }

    @Test
    public void testHandle_shouldReturnSuccessIfLinkTrack() {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.SUCCESS_LINK_UN_TRACK);
        assertThat(unTrackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(SUCCESS_LINK_UN_TRACK.getMessage());
    }

    @Test
    public void testHandle_shouldReturnFailIfLinkNoTrack() {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.FAIL_LINK_NOT_TRACK);
        assertThat(unTrackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.FAIL_LINK_NOT_TRACK.getMessage());
    }
}
