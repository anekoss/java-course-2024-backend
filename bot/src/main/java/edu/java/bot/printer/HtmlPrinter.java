package edu.java.bot.printer;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

@Component
public class HtmlPrinter implements Printer {

    public HtmlPrinter() {
    }

    @Override
    public SendMessage getMessage(@NotNull Long chatId, @NotNull String message) {
        return new SendMessage(chatId, message).parseMode(ParseMode.HTML);
    }

    @Override
    public String boldText(@NotNull String text) {
        return "<b>" + text + "</b>";
    }

    @Override
    public String urlText(@NotNull String text) {
        return "<a href=\"" + text + "\">" + text + "</a>";
    }

    @Override
    public String nextLine() {
        return "\n";
    }

    @Override
    public String commandDescriptionText(@NotNull String name, @NotNull String description) {
        return boldText(name) + " - " + description + nextLine();
    }

}
