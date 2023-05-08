package ru.yandex.practicum.filmorate.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.IOException;

public class GenreDeserializer extends StdDeserializer<Genre> {

    public GenreDeserializer() {
        this(null);
    }

    public GenreDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Genre deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        int id = (Integer) node.get("id").numberValue();
        return new Genre(id);
    }

}