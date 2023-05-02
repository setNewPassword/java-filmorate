package ru.yandex.practicum.filmorate.exception;

public class DatabaseResponseException extends RuntimeException {

    public DatabaseResponseException(String message) {
        super(message);
    }
}