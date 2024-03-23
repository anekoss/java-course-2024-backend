package edu.java.domain;

import lombok.Getter;

@Getter
public enum UpdateType {
    NEW_ANSWER("new answer"), NO_UPDATE(""), UPDATE("update");

    private final String message;

    UpdateType(String message){
        this.message = message;
    }
}
