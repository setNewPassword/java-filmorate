package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreStorage storage;

    @Autowired
    public GenreServiceImpl(GenreStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<Genre> getAll() {
        log.info("Запрошен полный список жанров.");
        return storage.getAll();
    }

    @Override
    public Genre getById(long id) {
        log.info("Запрошен жанр id = " + id + ".");
        return storage.findById(id).orElseThrow(() -> new GenreNotFoundException(
                String.format("Запрошен жанр с неизвестным id = %d.", id)
        ));
    }
}
