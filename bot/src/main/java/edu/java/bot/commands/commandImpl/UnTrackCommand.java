package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandExecutionStatus;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS;

@Component
public class UnTrackCommand implements Command {
    private final CommandService commandService;

    @Autowired
    public UnTrackCommand(CommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "прекратить отслеживание ссылки";
    }

    @Override
    public String handle(Update update, Printer printer) {
        Long id = update.message().from().id();
        if (supports(update)) {
            return "Введите URL-ссылку, чтобы прекратить отслеживать обновления.";
        }
        String url = GET_TEXT_REQUEST.apply(update.message().text());
        String okResponse = "Вы больше не отслеживаете сслыку!";
        CommandExecutionStatus result = commandService.unTrack(id, url);
        return result == SUCCESS ? okResponse : result.getMessage();
    }
}
