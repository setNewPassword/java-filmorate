package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.FilmorateRowMapper.GENRE_ROW_MAPPER;

@Repository("genreDbStorage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findById(long id) {
        var sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, GENRE_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAll() {
        var sqlQuery = "SELECT * FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER);
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }
}
