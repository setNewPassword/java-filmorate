package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class FilmRepositoryImpl implements FilmRepository {
    private final Map<Integer, Film> films;

    public FilmRepositoryImpl() {
        films = new HashMap<>();
    }
    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(int id) {
        return films.containsKey(id) ? Optional.of(films.get(id)) : Optional.empty();
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
