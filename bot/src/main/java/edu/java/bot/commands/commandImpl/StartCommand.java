package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

    private final CommandService commandService;

    @Autowired
    public StartCommand(CommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "получить информацию о боте";
    }

    @Override
    public String handle(Update update, Printer printer) {
        Long id = update.message().from().id();
        commandService.start(id);
        String response =
            "Привет! Это сервис для отслеживания обновлений контента по ссылкам.\n"
                + "Чтобы узнать о доступных коммандах введите /help.";
        return response;
    }
}
