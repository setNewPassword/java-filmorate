package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    List<Mpa> getAll();

    Mpa getById(long id);
}
