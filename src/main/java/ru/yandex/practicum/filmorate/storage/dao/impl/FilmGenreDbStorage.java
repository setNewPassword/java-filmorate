package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.FilmorateRowMapper.GENRE_ROW_MAPPER;

@Repository("filmGenreDbStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Film save(Film film) {
        var sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (:film_id, :genre_id)";
        SqlParameterSource[] batch = film.getGenres()
                .stream()
                .map(Genre::getId)
                .map(genreId -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", genreId))
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sqlQuery, batch);
        return film;
    }

    @Override
    public Map<Long, Set<Genre>> findAllByIds(Collection<Long> ids) {
        var sqlQuery = "SELECT film_id, genre_id FROM film_genre WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.queryForStream(sqlQuery, idParams, (rs, rowNum) ->
                        Map.entry(rs.getLong("film_id"), new Genre(rs.getInt("genre_id"))))
                .collect(HashMap::new, (map, entry) -> {
                            Set<Genre> genres = map.getOrDefault(entry.getKey(), new HashSet<>());
                            genres.add(entry.getValue());
                            map.put(entry.getKey(), genres);
                        },
                        HashMap::putAll);
    }

    @Override
    public List<Genre> getGenresByFilmId(long id) {
        var sqlQuery = "SELECT genre_id FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER, id);
    }

    @Override
    public void deleteByFilmId(long id) {
        var sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteAll() {
        var sqlQuery = "DELETE from film_genre";
        jdbcTemplate.update(sqlQuery);
    }
}
