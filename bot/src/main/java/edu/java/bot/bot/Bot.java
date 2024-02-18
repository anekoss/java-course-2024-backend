package edu.java.bot.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Bot implements AutoCloseable, UpdatesListener {
    private final TelegramBotImpl bot;
    private final SetMyCommands setMyCommands;
    private final ExecutorService executorService;
    private final UserMessageProcessor userMessageProcessor;

    @Autowired
    public Bot(
        TelegramBotImpl bot,
        UserMessageProcessor userMessageProcessor,
        SetMyCommands setMyCommands,
        ExecutorService executorService
    ) {
        this.bot = bot;
        this.userMessageProcessor = userMessageProcessor;
        this.setMyCommands = setMyCommands;
        this.executorService = executorService;
    }

    @EventListener({ContextRefreshedEvent.class})
    void start() {
        bot.execute(setMyCommands);
        bot.setUpdatesListener(this, e -> {
            if (e.response() != null) {
                log.error(String.valueOf(e.response().errorCode()));
                log.error(e.response().description());
            } else {
                log.error("Ops!", e);
            }
        });
    }

    @Override
    public int process(@NotNull List<Update> list) {
        for (Update update : list) {
            if (supportUpdate(update)) {
                executorService.execute(() -> {
                    SendMessage message = userMessageProcessor.process(update);
                    bot.execute(message);
                });
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Override
    public void close() {
        executorService.close();
    }

    private boolean supportUpdate(Update update) {
        return update != null && update.message() != null && update.message().text() != null
            && update.message().from() != null;
    }
}
