package ru.yandex.practicum.filmorate.integration.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class LikeDaoTest {

    static FilmDbStorage filmStorage;
    static UserDbStorage userStorage;
    Film film;
    User user;
    final LikeStorage likeStorage;

    @Autowired
    public LikeDaoTest(LikeStorage likeStorage, FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.likeStorage = likeStorage;
        filmStorage = filmDbStorage;
        userStorage = userDbStorage;
        createEnvironment();
        filmStorage.save(film);
        userStorage.save(user);
    }

    public void createEnvironment() {
        film = Film.builder()
                .id(0)
                .name("Titanic")
                .description("American epic romance and disaster film directed by James Cameron.")
                .releaseDate(LocalDate.of(1997, 12, 19))
                .duration(195)
                .mpa(new Mpa(1))
                .build();
        user = User.builder()
                .id(0)
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
    }

    @AfterEach
    void afterEach() {
        likeStorage.deleteAllLikes();
        filmStorage.clear();
        userStorage.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.saveLike(like);
        final List<Long> usersId = likeStorage.findUsersIdByFilmId(film.getId());

        assertThat(usersId)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(user.getId());
    }

    @Test
    void deleteById() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.saveLike(like);
        likeStorage.deleteLike(like);
        final List<Long> usersId = likeStorage.findUsersIdByFilmId(film.getId());

        assertThat(usersId)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testIsExist() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.saveLike(like);
        assertThat(likeStorage.isExist(like)).isTrue();

        likeStorage.deleteLike(like);
        assertThat(likeStorage.isExist(like)).isFalse();
    }
}