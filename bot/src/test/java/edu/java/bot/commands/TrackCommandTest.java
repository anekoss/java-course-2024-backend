package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandExecutionStatus;
import edu.java.bot.commands.commandImpl.TrackCommand;
import edu.java.bot.printer.HtmlPrinter;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TrackCommandTest {

    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command trackCommand = new TrackCommand(commandServiceMock);
    private static final Update updateMock = Mockito.mock(Update.class);
    private final Printer printer = new HtmlPrinter();

    @BeforeClass
    public static void init() {
        Message message = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(1L);
        when(updateMock.message()).thenReturn(message);
        when(updateMock.message().text()).thenReturn("/track");

    }

    @Test
    public void testHandleRequest() {
        assertThat(trackCommand.handle(updateMock, printer)).isEqualTo(
            "Введите URL-ссылку, чтобы отслеживать обновления.");
    }

    @Test
    public void testHandleInvalidLink() throws URISyntaxException, MalformedURLException {
        when(commandServiceMock.track(any(), any())).thenReturn(CommandExecutionStatus.LINK_INVALID);
        assertThat(trackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.LINK_INVALID.getMessage());
    }

    @Test
    public void testHandleSuccess() throws URISyntaxException, MalformedURLException {
        when(commandServiceMock.track(any(), any())).thenReturn(CommandExecutionStatus.SUCCESS);
        assertThat(trackCommand.handle(
            updateMock,
            printer
        )).isEqualTo("Cсылка успешно добавлена!");
    }

    @Test
    public void testHandleAlreadyTrackLink() {
        when(commandServiceMock.unTrack(any(), any())).thenReturn(CommandExecutionStatus.LINK_ALREADY_TRACK);
        assertThat(trackCommand.handle(
            updateMock,
            printer
        )).isEqualTo(CommandExecutionStatus.LINK_ALREADY_TRACK.getMessage());
    }
}
