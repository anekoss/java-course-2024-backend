package edu.java.controller.exception;

public class ChatAlreadyExistException extends Exception {

    @Override
    public String getMessage() {
        return "Такой пользователь уже зарегистрирован";
    }
}
