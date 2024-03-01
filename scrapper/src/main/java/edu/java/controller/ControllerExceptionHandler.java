package edu.java.controller;

import edu.java.exception.AlreadyExistException;
import edu.java.exception.AlreadyRegisterException;
import edu.java.exception.ChatNotFoundException;
import java.net.URISyntaxException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
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
    public ErrorMessage badRequestParameter(IllegalArgumentException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Ссылка не найдена")
    public ErrorMessage linkNotFound(ResourceNotFoundException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(AlreadyRegisterException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "повторная регистрация")
    public ErrorMessage userAlreadyRegister(AlreadyRegisterException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Чат не существует")
    public ErrorMessage chatNotFound(ChatNotFoundException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "повторное добавление ссылки")
    public ErrorMessage linkAlreadyAdded(AlreadyExistException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }
}
