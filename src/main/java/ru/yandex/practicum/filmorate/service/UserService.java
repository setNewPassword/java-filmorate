package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User create(User user);

    User update(User user);

    User addFriend(Long basicUserId, Long addingUserId);

    User removeFriend(Long basicUserId, Long removingUserId);

    List<User> getCommonFriends(Long basicUserId, Long secondUserId);

    User getUserById(Long userId);

    List<User> getUsersFriends(Long userId);
}