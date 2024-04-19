package edu.java.scheduler.dto;

import lombok.Getter;

@Getter
public enum UpdateType {
    UPDATE_ANSWER("Обновление: новый ответ"), NO_UPDATE(""), UPDATE("Обновление "),
    UPDATE_BRANCH("Обновление: новая ветка");
    private final String message;

    UpdateType(String message) {
        this.message = message;
    }
    }
