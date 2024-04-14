package edu.java.controller.exception;

public class LinkAlreadyExistException extends Exception {

    @Override
    public String getMessage() {
        return "Данная ссылка уже отслеживается";
    }
}
