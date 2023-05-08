package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j

public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           FilmGenreStorage filmGenreStorage,
                           LikeStorage likeStorage,
                           UserService userService) {
        this.filmStorage = filmStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("Запрошен полный список фильмов.");
        List<Film> films = filmStorage.getAllFilms();
        List<Long> filmsIds = films
                .stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAllByIds(filmsIds);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAllByIds(filmsIds);
        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
        }
        Collections.sort(films);
        return films;
    }

    @Override
    public Film create(Film film) {
        if (film.getId() != 0) {
            throw new ValidationException("Недопустимый ID для создания фильма");
        }
        film = filmStorage.save(film);
        if (!film.getGenres().isEmpty()) {
            film = filmGenreStorage.save(film);
        }
        log.info("Добавлен новый фильм: {}.", film);
        return film;
        /*Film createdFilm = film.toBuilder().build();
        if (createdFilm.getId() != 0) {
            throw new ValidationException("Недопустимый ID для создания фильма");
        }
        createdFilm = filmStorage.save(createdFilm);
        if (!createdFilm.getGenres().isEmpty()) {
            createdFilm = filmGenreStorage.save(createdFilm);
        }
        log.info("Добавлен новый фильм: {}.", createdFilm);
        return createdFilm;*/
    }

    @Override
    public Film update(Film film) {
        Film updatedFilm;
        if (film.getId() == 0) {
            throw new ValidationException("Недопустимый ID для обновления фильма");
        } else if (filmStorage.existsById(film.getId())) {
            updatedFilm = filmStorage.save(film);
            filmGenreStorage.deleteByFilmId(updatedFilm.getId());
            filmGenreStorage.save(updatedFilm);
            updatedFilm.getLikes().addAll(likeStorage.findUsersIdByFilmId(updatedFilm.getId()));
            log.info("Данные фильма изменены: {}.", updatedFilm);
            return updatedFilm;
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден.", film.getId()));
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.info("Запрошен фильм с id = {}.", filmId);
        Film film = getFilmFromRepositoryOrThrowException(filmId);
        film.getGenres().addAll(filmGenreStorage.getGenresByFilmId(filmId));
        film.getLikes().addAll(likeStorage.findUsersIdByFilmId(filmId));
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = getFilmFromRepositoryOrThrowException(filmId);
        User user = userService.getUserById(userId);
        Like like = new Like(filmId, userId);
        if (!likeStorage.isExist(like)) {
            likeStorage.saveLike(like);
            log.info("Пользователь с id = {} поставил лайк фильму с id = {}.", userId, filmId);
            film.getGenres().addAll(filmGenreStorage.getGenresByFilmId(filmId));
            film.getLikes().addAll(likeStorage.findUsersIdByFilmId(filmId));
        } else {
            throw new IncorrectRequestException(
                    String.format("У фильма с id = %d уже есть лайк от пользователя с id = %d.", filmId, userId));
        }
        return film;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film film = getFilmFromRepositoryOrThrowException(filmId);
        User user = userService.getUserById(userId);
        Like like = new Like(filmId, userId);
        if (likeStorage.isExist(like)) {
            likeStorage.deleteLike(like);
            log.info("Пользователь с id = {} удалил лайк фильму с id = {}.", userId, filmId);
            film.getGenres().addAll(filmGenreStorage.getGenresByFilmId(filmId));
            film.getLikes().addAll(likeStorage.findUsersIdByFilmId(filmId));
        } else {
            throw new IncorrectRequestException(
                    String.format("У фильма с id = %d нет лайка от пользователя с id = %d.", filmId, userId));
        }
        return film;
    }

    @Override
    public List<Film> getTopLikedFilms(Integer count) {
        log.info("Запрошены топ-{} фильмов.", count);
        List<Film> films = filmStorage.getAllFilms();
        List<Long> filmsIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAllByIds(filmsIds);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAllByIds(filmsIds);
        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
        }
        return films.stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmFromRepositoryOrThrowException(Long id) {
        return filmStorage
                .findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + id + " не найден."));
    }
}