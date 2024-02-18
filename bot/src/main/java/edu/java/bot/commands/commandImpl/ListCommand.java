package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import java.net.URL;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {
    private final CommandService commandService;

    @Autowired
    public ListCommand(CommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "показать список отслеживаемых ссылок";
    }

    @Override
    public String handle(Update update, Printer printer) {
        Long id = update.message().from().id();
        Set<URL> urls = commandService.list(id);
        if (urls != null && !urls.isEmpty()) {
            StringBuilder response =
                new StringBuilder().append(printer.boldText("Отслеживаемые сслыки:"))
                    .append(printer.nextLine());
            urls.forEach(url -> response.append(printer.urlText(url.toString())).append(printer.nextLine()));
            return response.toString();
        }
        return "Вы еще не отслеживаете сслыки!";

    }
}
