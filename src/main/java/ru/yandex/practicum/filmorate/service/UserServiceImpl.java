package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static Long idCounter = 0L;
    @Autowired
    private UserStorage repository;

    @Override
    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    @Override
    public User create(User user) {
        user.setId(++idCounter);
        log.info("Добавлен новый пользователь: {}", user);
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        User updatedUser;
        if (repository.findById(user.getId()).isPresent()) {
            updatedUser = repository.save(user);
            log.info("Данные пользователя изменены: {}", user);
        } else {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }
        return updatedUser;
    }

    @Override
    public User addFriend(Long basicUserId, Long addingUserId) {
        User basicUser;
        if (repository.findById(basicUserId).isPresent()) {
            basicUser = repository.findById(basicUserId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + basicUserId + " не найден.");
        }
        User addingUser;
        if (repository.findById(addingUserId).isPresent()) {
            addingUser = repository.findById(addingUserId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + addingUserId + " не найден.");
        }
        basicUser.getFriends().add(addingUserId);
        basicUser = repository.save(basicUser);
        addingUser.getFriends().add(addingUserId);
        repository.save(addingUser);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", basicUserId, addingUserId);
        return basicUser;
    }

    @Override
    public User removeFriend(Long basicUserId, Long removingUserId) {
        User basicUser;
        if (repository.findById(basicUserId).isPresent()) {
            basicUser = repository.findById(basicUserId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + basicUserId + " не найден.");
        }
        User removingUser;
        if (repository.findById(removingUserId).isPresent()) {
            removingUser = repository.findById(removingUserId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + removingUserId + " не найден.");
        }
        if (basicUser.getFriends().contains(removingUserId) &&
                removingUser.getFriends().contains(basicUserId)) {
            basicUser.getFriends().remove(removingUserId);
            basicUser = repository.save(basicUser);
            removingUser.getFriends().remove(basicUserId);
            repository.save(removingUser);
        } else {
            throw new UserNotFoundException("Попытка расторгнуть несуществующую дружбу, id: "
                    + basicUserId + " и id: " + removingUserId + ".");
        }
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}.", basicUserId, removingUserId);
        return basicUser;
    }

    @Override
    public List<User> getCommonFriends(Long basicUserId, Long secondUserId) {
        User basicUser;
        if (repository.findById(basicUserId).isPresent()) {
            basicUser = repository.findById(basicUserId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + basicUserId + " не найден.");
        }
        User secondUser;
        if (repository.findById(secondUserId).isPresent()) {
            secondUser = repository.findById(secondUserId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + secondUserId + " не найден.");
        }

        return basicUser
                .getFriends()
                .stream()
                .filter(secondUser.getFriends()::contains)
                .map(id -> repository
                        .findById(id)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден.")))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long userId) {
        return repository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    public List<User> getUsersFriends(Long userId) {
        User user;
        if (repository.findById(userId).isPresent()) {
            user = repository.findById(userId).get();
        } else {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return user
                .getFriends()
                .stream()
                .map(id -> repository
                        .findById(id)
                        .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден.")))
                .collect(Collectors.toList());
    }
}
