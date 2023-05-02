package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    Film save(Film film);

    List<Genre> getGenresByFilmId(long id);

    void deleteByFilmId(long id);

    void deleteAll();
}
