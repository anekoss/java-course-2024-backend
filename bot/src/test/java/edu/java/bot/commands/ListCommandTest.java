package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.client.exception.CustomServerErrorException;
import edu.java.bot.commands.commandImpl.ListCommand;
import edu.java.bot.printer.HtmlPrinter;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ListCommandTest {

    private static final Update updateMock = Mockito.mock(Update.class);
    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command listCommand = new ListCommand(commandServiceMock);
    private final Printer printer = new HtmlPrinter();

    @BeforeEach
    public void init() {
        Message message = Mockito.mock(Message.class);
        when(updateMock.message()).thenReturn(message);
        User user = Mockito.mock(User.class);
        when(message.from()).thenReturn(user);
        when(user.id()).thenReturn(1L);
    }

    @Test
    public void testHandle_shouldReturnNoLinkMessageIfNoTrackLinks() throws CustomServerErrorException {
        when(commandServiceMock.list(any())).thenReturn(Set.of());
        assertThat(listCommand.handle(updateMock, printer)).isEqualTo("Вы еще не отслеживаете сслыки!");
        when(commandServiceMock.list(any())).thenReturn(null);
        assertThat(listCommand.handle(updateMock, printer)).isEqualTo("Вы еще не отслеживаете сслыки!");
    }

    @Test
    public void testHandle_shouldCorrectlyReturnTrackLink()
        throws URISyntaxException, MalformedURLException, CustomServerErrorException {
        when(commandServiceMock.list(any())).thenReturn(Set.of(new URI("https://edu.tinkoff.ru")));
        String response =
            "<b>Отслеживаемые сслыки:</b>\n<a href=\"https://edu.tinkoff.ru\">https://edu.tinkoff.ru</a>\n";
        assertThat(listCommand.handle(updateMock, printer)).isEqualTo(response);
    }

    @Test
    public void testHandle_shouldThrowCustomServerExceptionIfServerError() throws CustomServerErrorException {
        when(commandServiceMock.list(any())).thenThrow(CustomServerErrorException.class);
        assertThrows(CustomServerErrorException.class, () -> listCommand.handle(updateMock, printer));
    }
}
