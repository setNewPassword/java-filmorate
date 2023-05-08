package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.serializer.MpaDeserializer;

@JsonDeserialize(using = MpaDeserializer.class)
@Getter
@EqualsAndHashCode(exclude = "name")
@ToString
public class Mpa {

    private int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
        this.name = MpaType.values()[id - 1].getName();
    }

    public void setId(int id) {
        this.id = id;
        this.name = MpaType.values()[id - 1].getName();
    }
}