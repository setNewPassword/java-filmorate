package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private static Long idCounter = 0L;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllById(Collection<Long> ids) {
        return ids.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id) {
        User user = users.remove(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("User not found: id = %d", id));
        }
    }

    @Override
    public void deleteAllById(Collection<Long> ids) {
        ids.forEach(users::remove);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    public User create(User user) {
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
}