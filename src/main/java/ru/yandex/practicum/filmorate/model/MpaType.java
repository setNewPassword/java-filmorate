package ru.yandex.practicum.filmorate.model;

public enum MpaType {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");
    private final String name;

    MpaType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}