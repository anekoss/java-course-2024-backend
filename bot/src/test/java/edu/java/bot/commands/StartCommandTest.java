package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import edu.java.bot.commands.commandImpl.StartCommand;
import edu.java.bot.printer.HtmlPrinter;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class StartCommandTest {

    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command startCommand = new StartCommand(commandServiceMock);
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
    public void testHandle_shouldReturnGreetingMessage() {
        assertThat(startCommand.handle(updateMock, printer)).isEqualTo(
            "Привет! Это сервис для отслеживания обновлений контента по ссылкам.\n"
                + "Чтобы узнать о доступных командах введите /help.");
    }

}
