package edu.java.bot.commands.commandImpl;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.printer.Printer;
import edu.java.bot.service.CommandService;
import java.net.URI;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommand implements Command {
    private final CommandService commandService;

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Показать список отслеживаемых ссылок";
    }

    @Override
    public String handle(Update update, Printer printer) {
        Long id = update.message().from().id();
        Set<URI> urls = commandService.list(id);
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
