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

public class FilmServiceImpl implements FilmService{

    private static Long idCounter = 0L;
    @Autowired
    private FilmStorage repository;
    @Override
    public List<Film> getAllFilms() {
        return repository.getAllFilms();
    }

    @Override
    public Film create(Film film) {
        film.setId(++idCounter);
        log.info("Добавлен новый фильм: {}", film);
        return repository.save(film);
    }

    @Override
    public Film update(Film film) {
        Film updatedFilm;
        if (repository.findById(film.getId()).isPresent()) {
            updatedFilm = repository.save(film);
            log.info("Данные фильма изменены: {}", updatedFilm);
        } else {
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        return updatedFilm;
    }

    @Override
    public Film getFilmById(Long filmId) {
        return repository
                .findById(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + filmId + " не найден."));
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film likedFilm;
        if (repository.findById(filmId).isPresent()) {
            likedFilm = repository.findById(filmId).get();
        } else {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден.");
        }
        likedFilm.getLikes().add(userId);
        likedFilm = repository.save(likedFilm);
        log.info("Пользователь с id {} поставил лайк фильму с id {}.", userId, filmId);
        return likedFilm;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        Film likedFilm;
        if (repository.findById(filmId).isPresent()) {
            likedFilm = repository.findById(filmId).get();
        } else {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден.");
        }
        likedFilm.getLikes().remove(userId);
        likedFilm = repository.save(likedFilm);
        log.info("Пользователь с id {} удалил лайк фильму с id {}.", userId, filmId);
        return likedFilm;
    }

    @Override
    public List<Film> getTopLikedFilms(Integer count) {
        return repository
                .getAllFilms()
                .stream()
                .sorted((f0, f1) -> {
                    int comp = (f0.getLikes().size()) - (f1.getLikes().size());
                    return comp;
                    })
                .limit(count)
                .collect(Collectors.toList());
    }
}
