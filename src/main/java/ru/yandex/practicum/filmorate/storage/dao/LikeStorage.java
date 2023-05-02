package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.List;

public interface LikeStorage {
    Like saveLike(Like like);

    List<Long> findUsersIdByFilmId(long id);

    void deleteLike(Like like);

    boolean isExist(Like like);

    void deleteAllLikes();
}
