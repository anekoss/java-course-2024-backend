package edu.java.bot.bot;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandManager;
import edu.java.bot.printer.Printer;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMessageProcessor {
    private final Printer printer;
    private final CommandManager commandManager;
    private final Map<Long, Command> prevCommands;

    SendMessage process(Update update) {
        Command command = commandManager.retrieveCommand(update.message().text().split(" ")[0]);
        Long id = update.message().from().id();
        String message;
        if (commandManager.isUnknownCommand(command)
            && prevCommands.get(id) != null) {
            message = prevCommands.get(id).handle(update, printer);
        } else {
            message = command.handle(update, printer);
        }
        prevCommands.put(id, command);
        return printer.getMessage(id, message);
    }
}
