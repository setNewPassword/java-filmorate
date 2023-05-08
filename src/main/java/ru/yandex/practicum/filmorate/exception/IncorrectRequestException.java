package ru.yandex.practicum.filmorate.exception;

public class IncorrectRequestException extends RuntimeException {

    public IncorrectRequestException(String message) {
        super(message);
    }
}