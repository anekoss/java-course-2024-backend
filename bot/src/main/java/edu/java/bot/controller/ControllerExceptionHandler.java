package edu.java.bot.controller;

import java.net.URISyntaxException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, URISyntaxException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Некорректные параметры запроса")
    public ErrorMessage badRequestParameter(
        IllegalArgumentException exception,
        WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

}
