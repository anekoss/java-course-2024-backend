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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UnTrackCommandTest {

    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command unTrackCommand = new UnTrackCommand(commandServiceMock);
    private static final Update updateMock = Mockito.mock(Update.class);
    private final Printer printer = new HtmlPrinter();
    private static Message message;

    @BeforeEach
    public void init() {
        message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(1L);
        when(updateMock.message()).thenReturn(message);
    }

    @Test
    public void testHandleRequest() {
        when(message.text()).thenReturn("/untrack");
        assertThat(unTrackCommand.handle(updateMock, printer)).isEqualTo(
            "Введите URL-ссылку, чтобы прекратить отслеживать обновления.");
    }

    @Test
    public void testHandleInvalidLink() {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.LINK_INVALID);
        assertThat(unTrackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.LINK_INVALID.getMessage());
    }

    @Test
    public void testHandleSuccess() {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.SUCCESS);
        assertThat(unTrackCommand.handle(
            updateMock,
            printer
        )).isEqualTo("Вы больше не отслеживаете сслыку!");
    }

    @Test
    public void testHandleNoTrackLink() {
        when(message.text()).thenReturn("test");
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.LINK_NOT_TRACK);
        assertThat(unTrackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.LINK_NOT_TRACK.getMessage());
    }
}
