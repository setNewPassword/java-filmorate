package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j

public class FilmServiceImpl implements FilmService{

    private static int idCounter;
    @Autowired
    private FilmRepository repository;
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
        Optional<Film> existingFilm = repository.findById(film.getId());
        if (existingFilm.isPresent()) {
            film = repository.save(film);
            log.info("Данные фильма изменены: {}", film);
        } else {
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден.");
        }
        return film;
    }
}
