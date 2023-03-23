package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
