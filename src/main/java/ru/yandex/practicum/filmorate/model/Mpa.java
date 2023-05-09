package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.serializer.MpaDeserializer;

@JsonDeserialize(using = MpaDeserializer.class)
@Getter
@EqualsAndHashCode
@ToString
public class Mpa {

    private int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
        this.name = MpaType.values()[id - 1].getName();
    }

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
        this.name = MpaType.values()[id - 1].getName();
    }
}