package edu.java.scheduler.dto;

import lombok.Getter;

@Getter
public enum UpdateType {
    UPDATE_ANSWER("Проверьте ответы. Есть обновление."), NO_UPDATE(""), UPDATE("Обновление "),
    UPDATE_BRANCH("Проверьте ветки. Есть обновление.");
    private final String message;

    UpdateType(String message) {
        this.message = message;
    }
}
