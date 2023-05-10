package ru.yandex.practicum.filmorate.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.IOException;

@Component
public class MpaDeserializer extends StdDeserializer<Mpa> {

    public MpaDeserializer() {
        this(null);
    }

    public MpaDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Mpa deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        int id = (Integer) node.get("id").numberValue();
        return new Mpa(id);
    }

}
