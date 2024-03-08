package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotImpl extends TelegramBot {
    public TelegramBotImpl(ApplicationConfig applicationConfig) {
        super(applicationConfig.telegramToken());
    }
}
