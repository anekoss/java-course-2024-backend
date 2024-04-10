package edu.java.bot.commands;

import lombok.Getter;

@Getter
public enum CommandExecutionStatus {
    SUCCESS_REGISTER("Вы успешно зарегистрированы"),
    SUCCESS_LINK_TRACK("Ссылка добавлена"),
    SUCCESS_LINK_UN_TRACK("Ссылка удалена"),
    SUCCESS("Успешно"),
    FAIL_USER_ALREADY_REGISTER("Пользователь уже зарегистирован"),
    FAIL_LINK_INVALID("Введите действительную ссылку, пожалуйста."),
    FAIL_LINK_ALREADY_TRACK("Вы уже отслеживаете такую ссылку!"),
    FAIL_LINK_NOT_TRACK("Вы не отслеживаете такую ссылку!");

    private final String message;

    CommandExecutionStatus(String message) {
        this.message = message;
    }

}
