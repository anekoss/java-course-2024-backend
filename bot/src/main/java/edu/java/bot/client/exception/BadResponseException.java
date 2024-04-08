package edu.java.bot.client.exception;

public class BadResponseException extends Exception {
    @Override
    public String getMessage() {
        return "Bad response was returned from the service";
    }
}
