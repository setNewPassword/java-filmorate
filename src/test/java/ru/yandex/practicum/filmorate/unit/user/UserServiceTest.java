package ru.yandex.practicum.filmorate.unit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorage;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @Mock
    UserStorage userStorage;
    @Mock
    FriendshipStorage friendshipStorage;
    @InjectMocks
    UserServiceImpl userService;
    User user1;
    User user2;
    static Random random = new Random();
    static User[] usersArray = new User[1];

    @BeforeEach
    public void createUsers() {
        user1 = User.builder()
                .id(random.nextInt(97))
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
        user2 = User.builder()
                .id(user1.getId() + 1)
                .email("volozh@yandex.ru")
                .login("volozh")
                .name("Аркадий")
                .birthday(LocalDate.of(1964, 2, 11))
                .build();
        usersArray[0] = null;
    }

    @Test
    void shouldAddUserAndReturnIt() {
        given(userStorage.save(any(User.class))).willReturn(user1);

        User savedUser = userService.create(user1);

        verify(userStorage).save(user1);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    void shouldReturnUserHasNameEqualsToLoginWhenNameIsEmpty() {
        given(userStorage.save(any(User.class))).willReturn(user1);

        user1.setName("");
        user1.setId(0);
        final User returnedUser = userService.create(user1);
        user1.setName(user1.getLogin());

        verify(userStorage).save(user1);
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getName()).isEqualTo(user1.getLogin());
    }

    @Test
    void shouldUpdateUserAndReturnIt() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(userStorage.save(user1)).willReturn(user1);

        final User updatedUser = userService.update(user1);

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).save(user1);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(user1);
    }

    @Test
    void shouldReturnAllUsers() {
        final List<User> users = List.of(user1, user2);
        given(userStorage.getAll()).willReturn(users);

        final List<User> allUsers = userService.getAllUsers();

        verify(userStorage).getAll();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(users.size());
        assertThat(allUsers).isEqualTo(users);
    }

    @Test
    void shouldGetUserById() {
        given(userStorage.findById(anyLong())).willReturn(Optional.of(user1));

        final User returnedUser = userService.getUserById(user1.getId());

        verify(userStorage).findById(user1.getId());
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user1);
    }

    @Test
    void shouldTrowUserNotFoundException() {
        given(userStorage.findById(anyLong())).willReturn(Optional.empty());

        final User[] users = new User[1];
        final Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            users[0] = userService.getUserById(user1.getId());
        });

        verify(userStorage).findById(user1.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String
                .format("Пользователь с id = %d не найден.", user1.getId()));
        assertThat(users[0]).isNull();
    }

    @Test
    void shouldAddFriend() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.TRUE);
        given(friendshipStorage.isExist(any(Friendship.class))).willReturn(Boolean.TRUE);
        given(userStorage.findById(user2.getId())).willReturn(Optional.of(user2));

        final User returnedUser = userService.addFriend(user1.getId(), user2.getId());

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        verify(friendshipStorage).isExist(new Friendship(user1.getId(), user2.getId()));
        verify(userStorage).findById(user2.getId());
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user2);
    }

    @Test
    void shouldTrowUserNotFoundExceptionWhenUser1NotExist() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                usersArray[0] = userService.addFriend(user1.getId(), user2.getId()));


        verify(userStorage).existsById(user1.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь с указанным ID не найден.");
        assertThat(user1.getFriends().size()).isEqualTo(0);
    }

    @Test
    void shouldTrowUserNotFoundExceptionWhenUser2NotExist() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                usersArray[0] = userService.addFriend(user1.getId(), user2.getId()));

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь с указанным ID не найден.");
        assertThat(user1.getFriends().size()).isEqualTo(0);
    }

    @Test
    void shouldRemoveFriend() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.TRUE);
        given(friendshipStorage.isExist(any(Friendship.class))).willReturn(Boolean.TRUE);
        given(userStorage.findById(anyLong())).willReturn(Optional.of(user2));

        final User returned = userService.removeFriend(user1.getId(), user2.getId());

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        verify(friendshipStorage).isExist(new Friendship(user1.getId(), user2.getId()));
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(user2);
        assertThat(user1.getFriends().size()).isEqualTo(0);
        assertThat(user1.getFriends()).isEqualTo(Collections.emptyList());
    }

    @Test
    void shouldThrowIncorrectRequestExceptionWhenRemoveFriendWithoutFriendship() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.TRUE);
        given(friendshipStorage.isExist(any(Friendship.class))).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(IncorrectRequestException.class, () ->
                usersArray[0] = userService.removeFriend(user1.getId(), user2.getId()));

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        verify(friendshipStorage).isExist(new Friendship(user1.getId(), user2.getId()));
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectRequestException.class);
        assertThat(exception.getMessage()).isEqualTo(String
                .format("Попытка расторгнуть несуществующую дружбу между пользователями с id = %d и id = %d.",
                        user1.getId(), user2.getId()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenRemoveUser1NotExist() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                usersArray[0] = userService.removeFriend(user1.getId(), user2.getId()));

        verify(userStorage).existsById(user1.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id = %d не найден.", user1.getId()));
        assertThat(user1.getFriends().size()).isEqualTo(0);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenRemoveUser2NotExist() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                usersArray[0] = userService.removeFriend(user1.getId(), user2.getId()));

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id = %d не найден.", user2.getId()));
        assertThat(user1.getFriends().size()).isEqualTo(0);
    }

    @Test
    void shouldReturnFriendsList() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(friendshipStorage.findFriendsIdByUserId(anyLong())).willReturn(List.of(user2.getId()));
        given(userStorage.getAllById(anyCollection())).willReturn(List.of(user2));

        final List<User> friendList = userService.getUsersFriends(user1.getId());

        verify(userStorage).existsById(user1.getId());
        verify(friendshipStorage).findFriendsIdByUserId(user1.getId());
        verify(userStorage).getAllById(List.of(user2.getId()));
        assertThat(friendList).isNotNull();
        assertThat(friendList.size()).isEqualTo(1);
        assertThat(friendList).isEqualTo(List.of(user2));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserNotExistAndGetFriends() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final List<User> Users = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                Users.addAll(userService.getUsersFriends(user1.getId())));

        verify(userStorage).existsById(user1.getId());
        assertThat(Users.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id = %d не найден.", user1.getId()));
    }

    @Test
    void shouldReturnCommonFriends() {
        User commonFriend = User
                .builder()
                .id(user2.getId() + 1)
                .email("brin@gmail.com")
                .name("Сергей")
                .login("googolplex")
                .birthday(LocalDate.of(1973, 8, 21))
                .build();
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.TRUE);
        given(friendshipStorage.findFriendsIdByUserId(user1.getId())).willReturn(List.of(commonFriend.getId()));
        given(friendshipStorage.findFriendsIdByUserId(user2.getId())).willReturn(List.of(commonFriend.getId()));
        given(userStorage.getAllById(anyCollection())).willReturn(List.of(commonFriend));

        final List<User> mutualFriendList = userService.getCommonFriends(user1.getId(), user2.getId());

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        verify(friendshipStorage).findFriendsIdByUserId(user1.getId());
        verify(friendshipStorage).findFriendsIdByUserId(user2.getId());
        verify(userStorage).getAllById(List.of(commonFriend.getId()));
        assertThat(mutualFriendList).isNotNull();
        assertThat(mutualFriendList.size()).isEqualTo(1);
        assertThat(mutualFriendList).isEqualTo(List.of(commonFriend));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUser1NotExistAndGetCommonFriends() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final List<User> users = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                users.addAll(userService.getCommonFriends(user1.getId(), user2.getId())));

        verify(userStorage).existsById(user1.getId());
        assertThat(users.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id = %d не найден.", user1.getId()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUser2NotExistAndGetCommonFriends() {
        given(userStorage.existsById(user1.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(user2.getId())).willReturn(Boolean.FALSE);

        final List<User> users = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                users.addAll(userService.getCommonFriends(user1.getId(), user2.getId())));

        verify(userStorage).existsById(user1.getId());
        verify(userStorage).existsById(user2.getId());
        assertThat(users.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id = %d не найден.", user2.getId()));
    }
}