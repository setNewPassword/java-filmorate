package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User create(User user);

    User update(User user);

    User addFriend(long basicUserId, long addingUserId);

    User removeFriend(long basicUserId, long removingUserId);

    List<User> getCommonFriends(long basicUserId, long secondUserId);

    User getUserById(long userId);

    List<User> getUsersFriends(long userId);

    boolean existsById(long id);
}