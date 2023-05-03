package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.FilmorateRowMapper.GENRE_ROW_MAPPER;

@Repository("filmGenreDbStorage")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        var sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        film.getGenres().stream()
                .map(Genre::getId)
                .forEach(genreId -> jdbcTemplate.update(sqlQuery, film.getId(), genreId));
        return film;
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
