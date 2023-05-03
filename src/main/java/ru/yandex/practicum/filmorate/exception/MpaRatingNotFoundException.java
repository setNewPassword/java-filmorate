package ru.yandex.practicum.filmorate.exception;

public class MpaRatingNotFoundException extends RuntimeException{
    public MpaRatingNotFoundException(String message) {
        super(message);
    }
}
