package edu.java.bot.client.exception;

public class CustomServerErrorException extends Exception {
    @Override
    public String getMessage() {
        return "Сервис временно не доступен. Пожалуйста, повтори запрос позже";
    }
}
