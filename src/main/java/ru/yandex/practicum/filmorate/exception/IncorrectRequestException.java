package ru.yandex.practicum.filmorate.exception;

public class IncorrectRequestException extends RuntimeException {
    public IncorrectRequestException() {
        super();
    }

    public IncorrectRequestException(Throwable throwable) {
        super(throwable);
    }

    public IncorrectRequestException(String message) {
        super(message);
    }

    public IncorrectRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}