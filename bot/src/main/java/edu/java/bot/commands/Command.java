package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.client.exception.CustomServerErrorException;
import edu.java.bot.printer.Printer;
import java.util.function.Function;

public interface Command {
    Function<String, String> GET_TEXT_REQUEST = str -> str.split(" ")[0];

    String command();

    String description();

    String handle(Update update, Printer printer) throws CustomServerErrorException;

    default boolean supports(Update update) {

        return update != null && update.message() != null && update.message().text() != null
            && update.message().from() != null && GET_TEXT_REQUEST.apply(update.message().text()).equals(command());
    }

    default BotCommand toApiCommand() {
        return new BotCommand(command(), description());
    }
}
