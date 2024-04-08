package edu.java.controller;

import edu.java.controller.exception.ChatAlreadyExistException;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import java.net.URISyntaxException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
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

    @ExceptionHandler(ChatAlreadyExistException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Повторная регистрация")
    public ErrorMessage userAlreadyRegister(ChatAlreadyExistException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Чат не существует")
    public ErrorMessage chatNotFound(ChatNotFoundException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(LinkAlreadyExistException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Повторное добавление ссылки")
    public ErrorMessage linkAlreadyAdded(LinkAlreadyExistException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Сервер не может обработать запрос к сайту")
    public ErrorMessage serverError(HttpServerErrorException exception, WebRequest request) {
        return new ErrorMessage(exception.getMessage());
    }
}
