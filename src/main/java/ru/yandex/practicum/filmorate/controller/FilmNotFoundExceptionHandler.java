package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.error.AppError;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

@ControllerAdvice
@Slf4j
public class FilmNotFoundExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> catchFilmNotFoundException(FilmNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
