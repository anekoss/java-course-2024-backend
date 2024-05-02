package edu.java.controller.exception;

public class RateLimitingException extends Exception {

    @Override
    public String getMessage() {
        return "Вы исчерпали лимит обращений к сервису";
    }
}
