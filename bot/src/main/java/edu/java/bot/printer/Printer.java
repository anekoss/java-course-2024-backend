package edu.java.bot.printer;

import com.pengrad.telegrambot.request.SendMessage;

public interface Printer extends Formatter {
    SendMessage getMessage(Long chatId, String message);

}
