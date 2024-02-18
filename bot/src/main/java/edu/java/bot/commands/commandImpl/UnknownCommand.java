package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnknownCommand implements Command {

    private final CommandService commandService;

    @Autowired(required = false)
    public UnknownCommand(CommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public String command() {
        return "";
    }

    @Override
    public String description() {
        return "Бот не знает такую команду :( \nИспользуйте /help чтобы узнать о доступных коммандах.";
    }

    @Override
    public String handle(Update update, Printer printer) {
        return description();
    }
}
