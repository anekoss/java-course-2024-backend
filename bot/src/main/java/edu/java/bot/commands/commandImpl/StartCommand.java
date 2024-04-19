package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.client.exception.CustomServerErrorException;
import edu.java.bot.commands.Command;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final CommandService commandService;

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Получить информацию о боте";
    }

    @Override
    public String handle(Update update, Printer printer) throws CustomServerErrorException {
        Long id = update.message().from().id();
        commandService.start(id);
        return "Привет! Это сервис для отслеживания обновлений контента по ссылкам.\n"
            + "Чтобы узнать о доступных командах введите /help.";

    }
}
