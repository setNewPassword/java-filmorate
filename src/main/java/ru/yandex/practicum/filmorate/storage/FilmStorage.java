package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film save(Film film);

    List<Film> getAllFilms();

    Optional<Film> findById(Long id);

    List<Film> findAllById(Collection<Long> ids);

    boolean deleteFilmById(Long id);

    boolean existsById(long id);

    void clear();
}