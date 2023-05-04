package ru.yandex.practicum.filmorate.exception;

public abstract class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

}
