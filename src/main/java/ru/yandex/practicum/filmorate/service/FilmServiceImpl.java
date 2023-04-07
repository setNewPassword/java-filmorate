package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

public class FilmServiceImpl implements FilmService {

    @Autowired
    private FilmStorage filmRepository;
    @Autowired
    private UserService userService;

    @Override
    public List<Film> getAllFilms() {
        log.info("Запрошен полный список фильмов.");
        return filmRepository.getAllFilms();
    }

    @Override
    public Film create(Film film) {
        log.info("Добавлен новый фильм: {}", film);
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        Film updatedFilm;
        getFilmFromRepositoryOrThrowException(film.getId());
        updatedFilm = filmRepository.save(film);
        log.info("Данные фильма изменены: {}", updatedFilm);
        return updatedFilm;
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.info("Запрошен фильм с id: {}", filmId);
        return getFilmFromRepositoryOrThrowException(filmId);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film likedFilm = getFilmFromRepositoryOrThrowException(filmId);
        likedFilm.getLikes().add(userService
                .getUserById(userId)
                .getId());
        likedFilm = filmRepository.save(likedFilm);
        log.info("Пользователь с id {} поставил лайк фильму с id {}.", userId, filmId);
        return likedFilm;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film likedFilm = getFilmFromRepositoryOrThrowException(filmId);
        likedFilm.getLikes().remove(userService
                .getUserById(userId)
                .getId());
        likedFilm = filmRepository.save(likedFilm);
        log.info("Пользователь с id {} удалил лайк фильму с id {}.", userId, filmId);
        return likedFilm;
    }

    @Override
    public List<Film> getTopLikedFilms(Integer count) {
        return filmRepository
                .getAllFilms()
                .stream()
                .sorted((f0, f1) -> ((f1.getLikes().size()) - (f0.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmFromRepositoryOrThrowException(Long id) {
        return filmRepository
                .findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + id + " не найден."));
    }
}