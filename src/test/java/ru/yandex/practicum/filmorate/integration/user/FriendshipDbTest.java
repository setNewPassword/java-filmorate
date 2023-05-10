package ru.yandex.practicum.filmorate.integration.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FriendshipDbTest {

    static UserDbStorage userStorage;
    User user1;
    User user2;
    final FriendshipStorage friendshipStorage;

    @Autowired
    public FriendshipDbTest(FriendshipStorage friendshipStorage, UserDbStorage userDbStorage) {
        this.friendshipStorage = friendshipStorage;
        userStorage = userDbStorage;
        userStorage.deleteAll();
        createEnvironment();
        userStorage.save(user1);
        userStorage.save(user2);
    }

    public void createEnvironment() {
        user1 = User.builder()
                .id(0)
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
        user2 = User.builder()
                .id(0)
                .email("volozh@yandex.ru")
                .login("volozh")
                .name("Аркадий")
                .birthday(LocalDate.of(1964, 2, 11))
                .build();
    }

    @AfterEach
    void afterEach() {
        friendshipStorage.deleteAll();
    }

    @Test
    void testSaveAndFindFriendsId() {
        final Friendship friendship = new Friendship(user1.getId(), user2.getId());

        friendshipStorage.save(friendship);

        final List<Long> user1Friends = friendshipStorage.findFriendsIdByUserId(user1.getId());
        final List<Long> user2Friends = friendshipStorage.findFriendsIdByUserId(user2.getId());

        assertThat(user1Friends)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(user2.getId());
        assertThat(user2Friends)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testFindFriendship() {
        final Friendship friendship = new Friendship(user1.getId(), user2.getId());
        final Friendship inverseFriendship = new Friendship(user2.getId(), user1.getId());
        friendshipStorage.save(friendship);

        final Optional<Friendship> optionalFriendship = friendshipStorage.findFriendship(inverseFriendship);

        assertThat(optionalFriendship)
                .hasValueSatisfying(friendShip -> assertThat(friendShip)
                        .hasFieldOrPropertyWithValue("userId", user1.getId())
                        .hasFieldOrPropertyWithValue("friendId", user2.getId()));
    }

    @Test
    void testCancelFriendship() {
        final Friendship friendship = new Friendship(user1.getId(), user2.getId());
        final Friendship inverseFriendship = new Friendship(user2.getId(), user1.getId());
        friendshipStorage.save(friendship);
        assertThat(friendshipStorage.findFriendship(friendship)).isPresent();

        friendshipStorage.cancelFriendship(friendship);

        assertThat(friendshipStorage.findFriendship(friendship)).isNotPresent();
        assertThat(friendshipStorage.findFriendship(inverseFriendship)).isNotPresent();
    }

    @Test
    void testIsExist() {
        final Friendship friendship = new Friendship(user1.getId(), user2.getId());
        final Friendship inverseFriendship = new Friendship(user2.getId(), user1.getId());
        friendshipStorage.save(friendship);

        final boolean areFriends = friendshipStorage.isExist(friendship);

        assertThat(areFriends).isTrue();
        assertThat(friendshipStorage.isExist(inverseFriendship)).isTrue();
    }

    @Test
    void testIsNotConfirmed() {
        final Friendship friendship = new Friendship(user1.getId(), user2.getId());
        final Friendship inverseFriendship = new Friendship(user2.getId(), user1.getId());
        friendshipStorage.save(friendship);

        final boolean isConfirmed = friendshipStorage.isConfirmed(friendship);

        assertThat(isConfirmed).isFalse();
        assertThat(friendshipStorage.isConfirmed(inverseFriendship)).isFalse();
    }

    @Test
    void testConfirm() {
        final Friendship friendship = new Friendship(user1.getId(), user2.getId());
        final Friendship inverseFriendship = new Friendship(user2.getId(), user1.getId());
        friendshipStorage.save(friendship);

        friendshipStorage.confirmFriendship(inverseFriendship);

        assertThat(friendshipStorage.isConfirmed(friendship)).isTrue();
        assertThat(friendshipStorage.isConfirmed(inverseFriendship)).isTrue();
    }
}