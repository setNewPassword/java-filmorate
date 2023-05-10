package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.AppError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllerErrorHandler {

    public static final AppError appError = new AppError();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public AppError handleHttpRequestMethodNotSupportedEx(HttpRequestMethodNotSupportedException exception) {
        appError.setData(exception.getMessage(), exception.getMethod());
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public AppError handleIncorrectRequestException(IncorrectRequestException exception) {
        appError.setData(exception.getMessage(), "");
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidationException(ValidationException exception) {
        appError.setData(exception.getMessage(), "");
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppError handleEntityNotFoundException(EntityNotFoundException exception) {
        appError.setData(exception.getMessage(), "");
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleInternalException(RuntimeException exception) {
        appError.setData(exception.getMessage(), "");
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected AppError handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> errorReport = new HashMap<>();
        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errorReport.put(fieldName, message);
                });
        appError.setData(errorReport.keySet().toString(), errorReport.values().toString());
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected AppError handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        appError.setData("Получен некорректный JSON", exception.getMessage());
        appendLog(exception);
        return appError;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = String.format("Параметр '%s' со значением '%s' не может быть приведен к типу '%s'",
                exception.getName(), exception.getValue(), exception.getRequiredType());
        appError.setData(message, exception.getMessage());
        appendLog(exception);
        return appError;
    }

    private void appendLog(Exception e) {
        log.warn("{} : {} -> {}",
                e.getClass().getSimpleName(),
                appError.getMessage(),
                appError.getDescription());
    }
}