package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Repository("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private static Long idCounter = 0L;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAllById(Collection<Long> ids) {
        List<Film> result = new ArrayList<>();
        for (Long id : ids) {
            result.add(films.get(id));
        }
        return result;
    }

    @Override
    public boolean deleteFilmById(Long id) {
        if (existsById(id)) {
            films.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    @Override
    public void clear() {
        films.clear();
    }

    @Override
    public Film create(Film film) {
        film.setId(++idCounter);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        return film;
    }
}