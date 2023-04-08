package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User addNew(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User updateExisting(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    @PutMapping("/{basicUserId}/friends/{addingUserId}")
    public User addFriend(@PathVariable Long basicUserId, @PathVariable Long addingUserId) {
        return userService.addFriend(basicUserId, addingUserId);
    }

    @DeleteMapping("/{basicUserId}/friends/{addingUserId}")
    public User removeFriend(@PathVariable Long basicUserId, @PathVariable Long addingUserId) {
        return userService.removeFriend(basicUserId, addingUserId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{basicUserId}/friends/common/{secondUserId}")
    public List<User> getCommonFriends(@PathVariable Long basicUserId, @PathVariable Long secondUserId) {
        return userService.getCommonFriends(basicUserId, secondUserId);
    }
}