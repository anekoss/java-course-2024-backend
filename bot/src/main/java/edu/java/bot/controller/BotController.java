package edu.java.bot.controller;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.TelegramBotImpl;
import edu.java.bot.controller.dto.LinkUpdateRequest;
import edu.java.bot.printer.Printer;
import jakarta.validation.Valid;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
@AllArgsConstructor
public class BotController {
    private final TelegramBotImpl bot;
    private final Printer printer;

    @PostMapping(path = "/updates", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> linkUpdate(@RequestBody @Valid LinkUpdateRequest request) {
        String message = printer.boldText(request.description()) + printer.nextLine()
            + printer.urlText(request.url());
        Arrays.stream(request.tgChatIds())
              .forEach(chatId -> bot.execute(new SendMessage(chatId, message)));
        return ResponseEntity.ok().build();
    }

}
