package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnknownCommand implements Command {

    private final CommandService commandService;

    @Override
    public String command() {
        return "";
    }

    @Override
    public String description() {
        return "Бот не знает такую команду :( \nИспользуйте /help чтобы узнать о доступных командах.";
    }

    @Override
    public String handle(Update update, Printer printer) {
        return description();
    }
}
