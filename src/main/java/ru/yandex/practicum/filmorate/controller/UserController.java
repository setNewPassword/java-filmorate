package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNew(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User updateExisting(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    @PutMapping("/{basicUserId}/friends/{addingUserId}")
    public User addFriend(@PathVariable long basicUserId, @PathVariable long addingUserId) {
        return userService.addFriend(basicUserId, addingUserId);
    }

    @DeleteMapping("/{basicUserId}/friends/{addingUserId}")
    public User removeFriend(@PathVariable long basicUserId, @PathVariable long addingUserId) {
        return userService.removeFriend(basicUserId, addingUserId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/{basicUserId}/friends/common/{secondUserId}")
    public List<User> getCommonFriends(@PathVariable long basicUserId, @PathVariable long secondUserId) {
        return userService.getCommonFriends(basicUserId, secondUserId);
    }
}