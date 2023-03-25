package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException() {
        super();
    }

    public FilmNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public FilmNotFoundException(String message) {
        super(message);
    }

    public FilmNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
