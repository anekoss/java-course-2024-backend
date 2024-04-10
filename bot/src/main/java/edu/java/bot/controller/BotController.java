package edu.java.bot.controller;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.TelegramBotImpl;
import edu.java.bot.controller.dto.LinkUpdateRequest;
import jakarta.validation.Valid;
import java.net.URISyntaxException;
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

    @PostMapping(path = "/updates", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> linkUpdate(@RequestBody @Valid LinkUpdateRequest request) throws URISyntaxException {
        Arrays.stream(request.tgChatIds())
              .forEach(chatId -> bot.execute(new SendMessage(chatId, request.description())));
        return ResponseEntity.ok().build();
    }

}
