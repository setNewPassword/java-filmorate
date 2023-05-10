package ru.yandex.practicum.filmorate.exception;

public class MpaRatingNotFoundException extends EntityNotFoundException {
    public MpaRatingNotFoundException(String message) {
        super(message);
    }
}
