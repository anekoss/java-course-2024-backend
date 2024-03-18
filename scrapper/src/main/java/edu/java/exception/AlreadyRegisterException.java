package edu.java.exception;

public class AlreadyRegisterException extends Exception {

    @Override
    public String getMessage() {
        return "Такой пользователь уже зарегистрирован";
    }
}
