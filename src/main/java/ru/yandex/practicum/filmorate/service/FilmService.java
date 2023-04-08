package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAllFilms();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(Long filmId);

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    List<Film> getTopLikedFilms(Integer count);
}