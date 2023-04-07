package ru.yandex.practicum.filmorate.model.user;

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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @Mock
    UserStorage repository;
    @InjectMocks
    UserServiceImpl service;
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
        given(repository.create(any(User.class))).willReturn(user1);

        User savedUser = service.create(user1);

        verify(repository).create(user1);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    void shouldUpdateUserAndReturnIt() {
        given(repository.create(user1)).willReturn(user1);
        given(repository.findById(anyLong())).willReturn(Optional.of(user1));
        given(repository.save(user2)).willReturn(user2);

        User savedUser = service.create(user1);
        long id = savedUser.getId();
        user2.setId(id);
        User updatedUser = service.update(user2);

        verify(repository).create(user1);
        verify(repository).save(user2);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(user2);
    }

    @Test
    void shouldReturnAllUsers() {
        List<User> users = List.of(user1, user2);
        given(repository.getAllUsers()).willReturn(users);

        List<User> allUsers = service.getAllUsers();

        verify(repository).getAllUsers();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(users.size());
        assertThat(allUsers).isEqualTo(users);
    }

    @Test
    void shouldGetUserById() {
        given(repository.findById(anyLong())).willReturn(Optional.of(user1));

        final User returnedUser = service.getUserById(user1.getId());

        verify(repository).findById(user1.getId());
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user1);
    }

    @Test
    void shouldTrowUserNotFoundException() {
        given(repository.findById(anyLong())).willReturn(Optional.empty());

        final User[] users = new User[1];
        final Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            users[0] = service.getUserById(user1.getId());
        });

        verify(repository).findById(user1.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь с id " + user1.getId() + " не найден.");
        assertThat(users[0]).isNull();
    }

    @Test
    void shouldAddFriend() {
        given(repository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(repository.findById(user2.getId())).willReturn(Optional.of(user2));
        given(repository.save(user1)).willReturn(user1);

        final User returnedUser = service.addFriend(user1.getId(), user2.getId());

        verify(repository).findById(user1.getId());
        verify(repository).findById(user2.getId());
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user1);
        assertThat(user1.getFriends().size()).isEqualTo(1);
        assertThat(user1.getFriends()).isEqualTo(Set.of(user2.getId()));
    }

    @Test
    void shouldTrowUserNotFoundExceptionWhenUser1NotExist() {
        given(repository.findById(user1.getId())).willReturn(Optional.empty());
        lenient().when(repository.findById(user2.getId())).thenReturn(Optional.of(user2));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            usersArray[0] = service.addFriend(user1.getId(), user2.getId());
        });

        verify(repository).findById(user1.getId());
        verify(repository, never()).findById(user2.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь с id " + user1.getId() + " не найден.");
        assertThat(user1.getFriends().size()).isEqualTo(0);
    }

    @Test
    void shouldTrowUserNotFoundExceptionWhenUser2NotExist() {
        given(repository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(repository.findById(user2.getId())).willReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            usersArray[0] = service.addFriend(user1.getId(), user2.getId());
        });

        verify(repository).findById(user1.getId());
        verify(repository).findById(user2.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь с id " + user2.getId() + " не найден.");
        assertThat(user1.getFriends().size()).isEqualTo(0);
    }

    @Test
    void shouldRemoveFriend() {
        when(repository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(repository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(repository.save(user1)).thenReturn(user1);

        service.addFriend(user1.getId(), user2.getId());

        assertThat(user1.getFriends().size()).isEqualTo(1);
        assertThat(user1.getFriends()).isEqualTo(Set.of(user2.getId()));

        final User returnedUser = service.removeFriend(user1.getId(), user2.getId());

        verify(repository, times(2)).findById(user1.getId());
        verify(repository, times(2)).findById(user2.getId());
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(user1);
        assertThat(user2.getFriends().size()).isEqualTo(0);
        assertThat(user2.getFriends()).isEqualTo(Collections.emptySet());
    }

    @Test
    void shouldThrowIncorrectRequestExceptionWhenRemoveFriendWithoutFriendship() {
        when(repository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(repository.findById(user2.getId())).thenReturn(Optional.of(user2));

        final Throwable exception = assertThrows(IncorrectRequestException.class, () -> {
            usersArray[0] = service.removeFriend(user1.getId(), user2.getId());
        });

        verify(repository).findById(user1.getId());
        verify(repository).findById(user2.getId());
        assertThat(usersArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectRequestException.class);
        assertThat(exception.getMessage()).isEqualTo(String
                .format("Пользователи id: %d и id: %d не являются друзьями.", user1.getId(), user2.getId()));
    }

    @Test
    void shouldReturnFriends() {
        given(repository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(repository.findById(user2.getId())).willReturn(Optional.of(user2));

        user1.getFriends().add(user2.getId());
        final List<User> friendList = service.getUsersFriends(user1.getId());

        assertThat(friendList).isNotNull();
        assertThat(friendList.size()).isEqualTo(1);
        assertThat(friendList).isEqualTo(List.of(user2));
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
        given(repository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(repository.findById(user2.getId())).willReturn(Optional.of(user2));
        given(repository.findById(commonFriend.getId())).willReturn(Optional.of(commonFriend));

        user1.getFriends().add(commonFriend.getId());
        user2.getFriends().add(commonFriend.getId());
        final List<User> commonFriendSet = service.getCommonFriends(user1.getId(), user2.getId());

        verify(repository).findById(user1.getId());
        verify(repository).findById(user2.getId());
        assertThat(commonFriendSet).isNotNull();
        assertThat(commonFriendSet.size()).isEqualTo(1);
        assertThat(commonFriendSet).isEqualTo(List.of(commonFriend));
        assertThat(user1.getFriends().size()).isEqualTo(1);
        assertThat(user1.getFriends()).isEqualTo(Set.of(commonFriend.getId()));
        assertThat(user2.getFriends().size()).isEqualTo(1);
        assertThat(user2.getFriends()).isEqualTo(Set.of(commonFriend.getId()));
        assertThat(user1.getFriends()).isEqualTo(user2.getFriends());
    }
}