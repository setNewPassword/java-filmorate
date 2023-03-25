package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users;

    public UserRepositoryImpl() {
        users = new HashMap<>();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
