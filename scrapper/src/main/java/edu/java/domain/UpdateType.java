package edu.java.domain;

import lombok.Getter;

@Getter
public enum UpdateType {
    NEW_ANSWER("new answer"), NO_UPDATE(""), UPDATE("update"), NEW_BRANCH("new branch");

    private final String message;

    UpdateType(String message) {
        this.message = message;
    }
}
