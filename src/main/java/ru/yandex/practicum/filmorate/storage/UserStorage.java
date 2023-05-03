package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    Optional<User> findById(long id);

    List<User> getAll();

    List<User> getAllById(Collection<Long> ids);

    void deleteById(long id);

    void deleteAllById(Collection<Long> ids);

    void deleteAll();

    boolean existsById(long id);
}