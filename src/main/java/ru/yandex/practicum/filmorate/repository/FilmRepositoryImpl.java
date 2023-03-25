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
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
