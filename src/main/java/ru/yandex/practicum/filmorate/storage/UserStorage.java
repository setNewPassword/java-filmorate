package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    List<User> getAllUsers();

    Optional<User> findById(Long id);

    List<User> findAllById(Collection<Long> ids);

    void deleteById(long id);

    void deleteAllById(Collection<Long> ids);

    void deleteAll();

    boolean existsById(long id);
}