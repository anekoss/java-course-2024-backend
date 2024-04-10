package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.commandImpl.HelpCommand;
import edu.java.bot.commands.commandImpl.ListCommand;
import edu.java.bot.commands.commandImpl.StartCommand;
import edu.java.bot.commands.commandImpl.TrackCommand;
import edu.java.bot.commands.commandImpl.UnTrackCommand;
import edu.java.bot.commands.commandImpl.UnknownCommand;
import edu.java.bot.printer.HtmlPrinter;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

public class HelpCommandTest {

    private final CommandService commandServiceMock = Mockito.mock(CommandService.class);
    private final Command helpCommand = new HelpCommand(commandServiceMock);
    private final Update updateMock = Mockito.mock(Update.class);
    private final Printer printer = new HtmlPrinter();

    @Test
    public void testHandleNoAvailableCommands() {
        assertThat(helpCommand.handle(updateMock, printer)).isEqualTo(
            "В данный момент нет доступных команд.");
    }

    @Test
    public void testHandle_shouldReturnHelpMessage() {
        String response = "<b>Список доступных команд:</b>\n<b>/list</b> - Показать список отслеживаемых ссылок\n" +
            "<b>/help</b> - Вывести описание команд\n<b>/track</b> - Начать отслеживание ссылки\n" +
            "<b>/start</b> - Получить информацию о боте\n<b>/untrack</b> - Прекратить отслеживание ссылки\n";
        TrackCommand trackCommand = new TrackCommand(commandServiceMock);
        UnTrackCommand unTrackCommand = new UnTrackCommand(commandServiceMock);
        ListCommand listCommand = new ListCommand(commandServiceMock);
        StartCommand startCommand = new StartCommand(commandServiceMock);
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put(startCommand.command(), startCommand);
        commandMap.put(helpCommand.command(), helpCommand);
        commandMap.put(trackCommand.command(), trackCommand);
        commandMap.put(unTrackCommand.command(), unTrackCommand);
        commandMap.put(listCommand.command(), listCommand);
        CommandManager commandManager = new CommandManager(commandMap, new UnknownCommand(commandServiceMock));
        assertThat(helpCommand.handle(updateMock, printer)).isEqualTo(response);
    }

}
