package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.model.FilmorateRowMapper.LIKE_ROW_MAPPER;

@Repository("likeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Like saveLike(Like like) {
        var sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
        var likeParams = new BeanPropertySqlParameterSource(like);
        namedParameterJdbcTemplate.update(sqlQuery, likeParams);
        return like;
    }

    @Override
    public Map<Long, Set<Long>> findAllByIds(Collection<Long> ids) {
        var sqlQuery = "SELECT film_id, user_id FROM likes WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList(sqlQuery, idParams);
        Map<Long, Set<Long>> filmsLikes = new HashMap<>();
        for (var mapRow : resultSet) {
            long filmId = (Integer) mapRow.get("film_id");
            long userId = (Integer) mapRow.get("user_id");
            if (!filmsLikes.containsKey(filmId)) {
                filmsLikes.put(filmId, new HashSet<>());
            }
            filmsLikes.get(filmId).add(userId);
        }
        return filmsLikes;
    }

    @Override
    public List<Long> findUsersIdByFilmId(long id) {
        var sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, id);
    }

    @Override
    public void deleteLike(Like like) {
        var sqlQuery = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
        var likeParams = new BeanPropertySqlParameterSource(like);
        namedParameterJdbcTemplate.update(sqlQuery, likeParams);
    }

    @Override
    public boolean isExist(Like like) {
        var sqlQuery = "SELECT * FROM likes WHERE film_id = :filmId AND user_id = :userId";
        var likeParams = new BeanPropertySqlParameterSource(like);
        return namedParameterJdbcTemplate.query(sqlQuery, likeParams, LIKE_ROW_MAPPER).size() == 1;
    }

    @Override
    public void deleteAllLikes() {
        var sqlQuery = "DELETE FROM likes";
        jdbcTemplate.update(sqlQuery);
    }
}
