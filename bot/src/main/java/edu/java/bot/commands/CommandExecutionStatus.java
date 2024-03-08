package edu.java.bot.commands;

import lombok.Getter;

@Getter
public enum CommandExecutionStatus {
    SUCCESS("Успешно"),
    USER_ALREADY_REGISTER("Пользователь уже зарегистирован"),
    LINK_INVALID("Введите действительную ссылку, пожалуйста."),
    LINK_ALREADY_TRACK("Вы уже отслеживаете такую ссылку!"),
    LINK_NOT_TRACK("Вы не отслеживаете такую ссылку!");

    private final String message;

    CommandExecutionStatus(String message) {
        this.message = message;
    }

}
