package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }
    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}