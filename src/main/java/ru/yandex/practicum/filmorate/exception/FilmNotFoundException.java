package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends EntityNotFoundException {

    public FilmNotFoundException(String message) {
        super(message);
    }
}