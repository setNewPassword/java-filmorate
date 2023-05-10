package ru.yandex.practicum.filmorate.model;

public enum GenreType {

    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String name;

    GenreType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}