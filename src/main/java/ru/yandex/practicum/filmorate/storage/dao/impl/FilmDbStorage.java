package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DatabaseResponseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.FilmorateRowMapper.FILM_ROW_MAPPER;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }
    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery;
        if (film.getId() == 0) {
            sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                    "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";
        } else {
            sqlQuery = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
                    "duration = :duration, mpa_id = :mpaId WHERE film_id = :id";
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource filmParams = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());
        int numRows = namedParameterJdbcTemplate.update(sqlQuery, filmParams, keyHolder);
        if (numRows == 0) {
            throw new DatabaseResponseException(
                    String.format("Ошибка при попытке добавить в базу данных фильм с id = %d.", film.getId())
            );
        } else if (film.getId() == 0){
            long autoGeneratedKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(autoGeneratedKey);
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM films ORDER BY film_id";
        try {
            return jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, FILM_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAllById(Collection<Long> ids) {
        List<Film> result = new ArrayList<>();
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        for (Long id : ids) {
            result.add(jdbcTemplate.queryForObject(sqlQuery, FILM_ROW_MAPPER, id));
        }
        return result;
    }

    @Override
    public boolean deleteFilmById(Long id) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
            int rows = jdbcTemplate.update(sqlQuery);
        return rows != 0;
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }
}
