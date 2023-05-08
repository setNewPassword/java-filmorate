package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;

import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.model.FilmorateRowMapper.MPA_ROW_MAPPER;

@Repository("mpaDbStorage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> findById(long id) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, MPA_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getAll() {
        var sqlQuery = "SELECT * FROM mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sqlQuery, MPA_ROW_MAPPER);
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }
}
