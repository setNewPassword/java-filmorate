package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.GenreDeserializer;

@JsonDeserialize(using = GenreDeserializer.class)
@Data
public class Genre {

    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
        this.name = GenreType.values()[id - 1].getName();
    }
}