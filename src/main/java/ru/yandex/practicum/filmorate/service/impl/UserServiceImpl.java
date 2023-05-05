package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Запрошен список всех пользователей.");
        return userStorage.getAll();
    }

    @Override
    public User create(User user) {
        User verifiedUser = user.toBuilder().build();
        User createdUser = userStorage.save(verifiedUser);
        log.info("Добавлен новый пользователь: {}.", createdUser);
        return createdUser;
    }

    @Override
    public User getUserById(long userId) {
        log.info("Запрошен пользователь с id: {}.", userId);
        return getUserFromRepositoryOrThrowException(userId);
    }

    @Override
    public User update(User user) {
        User verifiedUser = user.toBuilder().build();
        if (userStorage.existsById(verifiedUser.getId())) {
            User updatedUser = userStorage.save(verifiedUser);
            log.info("Данные пользователя изменены: {}.", updatedUser);
            return updatedUser;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", user.getId()));
        }
    }

    @Override
    public User addFriend(long basicUserId, long addingUserId) {
        if (existsById(basicUserId) && existsById(addingUserId)) {
            Friendship friendship = new Friendship(basicUserId, addingUserId);
            if (!friendshipStorage.isExist(friendship)) {
                friendshipStorage.save(friendship);
                log.info("Пользователь с id = {} отправил запрос на дружбу пользователю с id = {}.",
                        basicUserId, addingUserId);
            } else if (!friendshipStorage.isConfirmed(friendship)) {
                friendshipStorage.confirmFriendship(friendship);
                log.info("Подтверждена дружба между пользователями id = {} и  id = {}.", basicUserId, addingUserId);
            } else {
                throw new IncorrectRequestException(String
                        .format("Пользователи id: %d и id: %d уже являются друзьями.", basicUserId, addingUserId));
            }
            return getUserFromRepositoryOrThrowException(addingUserId);
        } else {
            throw new UserNotFoundException("Пользователь с указанным ID не найден.");
        }
    }

    @Override
    public User removeFriend(long basicUserId, long removingUserId) {
        validateId(basicUserId);
        validateId(removingUserId);
        Friendship friendship = new Friendship(basicUserId, removingUserId);
        if (friendshipStorage.isExist(friendship)) {
            friendshipStorage.cancelFriendship(friendship);
            log.info("Пользователь с id = {} и пользователь с id = {} больше не являются друзьями.",
                    basicUserId, removingUserId);
        } else {
            throw new IncorrectRequestException(String
                    .format("Попытка расторгнуть несуществующую дружбу между пользователями с id = %d и id = %d.",
                            basicUserId, removingUserId));
        }
        return getUserFromRepositoryOrThrowException(basicUserId);
    }

    @Override
    public List<User> getCommonFriends(long basicUserId, long secondUserId) {
        log.info("Запрошен список общих друзей пользователя с id = {} и пользователя с id = {}.", basicUserId, secondUserId);
        validateId(basicUserId);
        validateId(secondUserId);
        Collection<Long> basicUserFriendsId = friendshipStorage.findFriendsIdByUserId(basicUserId);
        List<Long> commonFriendsId = friendshipStorage.findFriendsIdByUserId(secondUserId)
                .stream()
                .filter(basicUserFriendsId::contains)
                .collect(Collectors.toList());
        return userStorage.getAllById(commonFriendsId);
    }

    @Override
    public List<User> getUsersFriends(long userId) {
        log.info("Запрошен список друзей пользователя с id = {}.", userId);
        validateId(userId);
        return userStorage.getAllById(friendshipStorage.findFriendsIdByUserId(userId));
    }

    @Override
    public boolean existsById(long id) {
        return userStorage.existsById(id);
    }

    private User getUserFromRepositoryOrThrowException(long id) {
        return userStorage
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id = %d не найден.", id)));
    }

    private void validateId(long id) {
        if (!userStorage.existsById(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", id));
        }
    }
}