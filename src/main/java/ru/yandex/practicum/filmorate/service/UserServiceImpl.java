package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserStorage repository;

    @Override
    public List<User> getAllUsers() {
        log.info("Запрошен список всех пользователей.");
        return repository.getAllUsers();
    }

    @Override
    public User create(User user) {
        log.info("Добавлен новый пользователь: {}", user);
        return repository.create(user);
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Запрошен пользователь с id: {}", userId);
        return getUserFromRepositoryOrThrowException(userId);
    }

    @Override
    public User update(User user) {
        User updatedUser;
        getUserFromRepositoryOrThrowException(user.getId());
        updatedUser = repository.save(user);
        log.info("Данные пользователя изменены: {}", user);
        return updatedUser;
    }

    @Override
    public User addFriend(Long basicUserId, Long addingUserId) {
        User basicUser = getUserFromRepositoryOrThrowException(basicUserId);
        User addingUser = getUserFromRepositoryOrThrowException(addingUserId);
        if (basicUser.getFriends().contains(addingUserId)) {
            throw new IncorrectRequestException(String
                    .format("Пользователи id: %d и id: %d уже являются друзьями.", basicUserId, addingUserId));
        } else {
            basicUser.getFriends().add(addingUserId);
            basicUser = repository.save(basicUser);
            addingUser.getFriends().add(basicUserId);
            repository.save(addingUser);
            log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", basicUserId, addingUserId);
            return basicUser;
        }
    }

    @Override
    public User removeFriend(Long basicUserId, Long removingUserId) {
        User basicUser = getUserFromRepositoryOrThrowException(basicUserId);
        User removingUser = getUserFromRepositoryOrThrowException(removingUserId);
        if (basicUser.getFriends().contains(removingUserId) &&
                removingUser.getFriends().contains(basicUserId)) {
            basicUser.getFriends().remove(removingUserId);
            basicUser = repository.save(basicUser);
            removingUser.getFriends().remove(basicUserId);
            repository.save(removingUser);
        } else {
            throw new IncorrectRequestException(String
                    .format("Пользователи id: %d и id: %d не являются друзьями.", basicUserId, removingUserId));
        }
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}.", basicUserId, removingUserId);
        return basicUser;
    }

    @Override
    public List<User> getCommonFriends(Long basicUserId, Long secondUserId) {
        User basicUser = getUserFromRepositoryOrThrowException(basicUserId);
        User secondUser = getUserFromRepositoryOrThrowException(secondUserId);
        log.info("Запрошен список общих друзей пользователя с id {} и пользователя с id {}.", basicUserId, secondUserId);
        return basicUser
                .getFriends()
                .stream()
                .filter(secondUser.getFriends()::contains)
                .map(this::getUserFromRepositoryOrThrowException)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getUsersFriends(Long userId) {
        User user = getUserFromRepositoryOrThrowException(userId);
        log.info("Запрошен список друзей пользователя с id: {}", userId);
        return user
                .getFriends()
                .stream()
                .map(this::getUserFromRepositoryOrThrowException)
                .collect(Collectors.toList());
    }

    private User getUserFromRepositoryOrThrowException(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден."));
    }
}