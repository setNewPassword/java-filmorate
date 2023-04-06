package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.error.AppError;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice
@Slf4j
public class ControllerErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidationException(final ValidationException e) {
        log.warn("400 {}", e.getMessage());
        return  new AppError(400, e.getMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppError handleNotFoundException(final RuntimeException e) {
        log.warn("404 {}", e.getMessage());
        return new AppError(404, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleInternalException(final RuntimeException e) {
        log.warn("500 {}", e.getMessage());
        return new AppError(500, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected AppError handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                    HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("400 {}", e.getMessage());
        return new AppError(400, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("Параметр '%s' со значением '%s' не может быть приведен к типу '%s'",
                e.getName(), e.getValue(), e.getRequiredType());
        log.warn("400 {}", e.getMessage());
        return new AppError(400, message + ": " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AppError handleIncorrectRequestException(IncorrectRequestException e) {
        log.warn("406 {}", e.getMessage());
        return new AppError(406, e.getMessage());
    }
}