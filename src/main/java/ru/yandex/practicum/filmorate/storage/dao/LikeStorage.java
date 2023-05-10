package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LikeStorage {
    Like saveLike(Like like);

    Map<Long, Set<Long>> findAllByIds(Collection<Long> ids);

    List<Long> findUsersIdByFilmId(long id);

    void deleteLike(Like like);

    boolean isExist(Like like);

    void deleteAllLikes();
}
