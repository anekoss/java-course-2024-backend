package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.TelegramBotImpl;
import edu.java.bot.controller.dto.LinkUpdateRequest;
import edu.java.bot.printer.Printer;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatesSenderService {
    private final TelegramBotImpl bot;
    private final Printer printer;

    public void sendUpdates(LinkUpdateRequest request) {
        String message = printer.boldText(request.description()) + printer.nextLine()
            + printer.urlText(request.url());
        Arrays.stream(request.tgChatIds())
            .forEach(chatId -> bot.execute(new SendMessage(chatId, message)));
    }
}
