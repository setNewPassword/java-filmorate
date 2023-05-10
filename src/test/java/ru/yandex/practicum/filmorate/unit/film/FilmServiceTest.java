package ru.yandex.practicum.filmorate.unit.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectRequestException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {
    @Mock
    @Qualifier("filmDbStorage")
    FilmStorage filmStorage;
    @Mock
    UserService userService;
    @Mock
    FilmGenreStorage filmGenreStorage;
    @Mock
    LikeStorage likeStorage;
    @InjectMocks
    FilmServiceImpl filmService;
    static Random random = new Random();
    Film film1;
    Film film2;
    User user;
    static Film[] filmsArray = new Film[1];

    @BeforeEach
    public void createEnvironment() {
        film1 = Film.builder()
                .id(random.nextInt(97))
                .name("Titanic")
                .description("American epic romance and disaster film directed by James Cameron.")
                .releaseDate(LocalDate.of(1997, 12, 19))
                .duration(195)
                .mpa(new Mpa(1))
                .build();
        film2 = Film.builder()
                .id(random.nextInt(97))
                .name("The Shawshank Redemption")
                .description("American drama film directed by Frank Darabont, based on the 1982 Stephen King novella.")
                .releaseDate(LocalDate.of(1994, 9, 23))
                .duration(142)
                .mpa(new Mpa(2))
                .build();
        user = User.builder()
                .id(random.nextInt(97) + 100)
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
        filmsArray[0] = null;
    }

    @Test
    void shouldAddFilmAndReturnIt() {
        film1.setId(0);
        given(filmStorage.save(any(Film.class))).willReturn(film1);

        Film film = filmService.create(film1);

        verify(filmStorage).save(film1);
        assertThat(film).isNotNull();
        assertThat(film).isEqualTo(film1);
    }

    @Test
    void shouldUpdateFilmAndReturnIt() {
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(filmStorage.save(film1)).willReturn(film1);

        final Film updatedFilm = filmService.update(film1);

        verify(filmStorage).save(film1);
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(film1);
    }

    @Test
    void shouldReturnAllFilms() {
        final List<Film> films = new ArrayList<>(List.of(film1, film2));
        given(filmStorage.getAllFilms()).willReturn(films);

        final List<Film> allFilms = filmService.getAllFilms();

        verify(filmStorage).getAllFilms();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(films.size());
        assertThat(allFilms).isEqualTo(films);
    }

    @Test
    void shouldThrowFilmNotFoundExceptionWhenFilmNotExist() {
        film1.setId(1);
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                filmsArray[0] = filmService.update(film1));

        verify(filmStorage).existsById(anyLong());
        assertThat(filmsArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id = %d не найден.", film1.getId()));
    }

    @Test
    void shouldReturnFilmObjectWhenGetFilmById() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(film1));

        final Film returned = filmService.getFilmById(film1.getId());

        verify(filmStorage).findById(film1.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(film1);
    }

    @Test
    void shouldThrowFilmNotFoundExceptionWhenFilmIdNotPresentInStorage() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.empty());

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                filmsArray[0] = filmService.getFilmById(film1.getId()));

        verify(filmStorage).findById(film1.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id %d не найден.", film1.getId()));
        assertThat(filmsArray[0]).isNull();
    }

    @Test
    void shouldReturnFilmObjectWhenAddLike() {
        film1.setId(1);
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(film1));
        given(userService.getUserById(anyLong())).willReturn(user);
        given(likeStorage.isExist(any(Like.class))).willReturn(Boolean.FALSE);

        final Film returned = filmService.addLike(film1.getId(), user.getId());

        verify(filmStorage).findById(film1.getId());
        verify(userService).getUserById(user.getId());
        verify(likeStorage).isExist(new Like(film1.getId(), user.getId()));
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(film1);
    }

    @Test
    void shouldThrowFilmNotFoundExceptionWhenFilmIdNotPresentInStorageAndAddLike() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.empty());
        lenient().when(userService.getUserById(anyLong())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                filmsArray[0] = filmService.addLike(film1.getId(), user.getId()));

        verify(filmStorage).findById(film1.getId());
        verify(userService, never()).getUserById(anyLong());
        assertThat(filmsArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id %d не найден.", film1.getId()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenAddLikeAndUserIdNotPresent() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(film1));
        given(userService.getUserById(anyLong())).willThrow(new UserNotFoundException("Пользователь не найден."));

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                filmsArray[0] = filmService.addLike(film1.getId(), user.getId()));

        verify(filmStorage).findById(film1.getId());
        verify(userService).getUserById(user.getId());
        assertThat(filmsArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден.");
    }

    @Test
    void shouldReturnFilmObjectWhenRemoveLike() {
        film1.setId(1);
        user.setId(1);
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(film1));
        given(userService.getUserById(anyLong())).willReturn(user);
        given(likeStorage.isExist(any(Like.class))).willReturn(Boolean.TRUE);

        final Film returned = filmService.removeLike(film1.getId(), user.getId());

        verify(filmStorage).findById(film1.getId());
        verify(userService).getUserById(user.getId());
        verify(likeStorage).isExist(new Like(film1.getId(), user.getId()));
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(film1);
    }

    @Test
    void shouldThrowIncorrectRequestExceptionWhenUserDontLikeFilmAndRemoveLike() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(film1));
        given(userService.getUserById(anyLong())).willReturn(user);
        given(likeStorage.isExist(any(Like.class))).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(IncorrectRequestException.class, () ->
                filmsArray[0] = filmService.removeLike(film1.getId(), user.getId()));

        verify(filmStorage).findById(film1.getId());
        verify(userService).getUserById(user.getId());
        verify(likeStorage).isExist(new Like(film1.getId(), user.getId()));
        assertThat(filmsArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectRequestException.class);
        assertThat(exception.getMessage()).isEqualTo(
                String.format("У фильма с id = %d нет лайка от пользователя с id = %d.", film1.getId(), user.getId()));
    }

    @Test
    void shouldThrowFilmNotFoundExceptionWhenFilmIdNotPresentInStorageAndRemoveLike() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.empty());
        lenient().when(userService.getUserById(anyLong())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                filmsArray[0] = filmService.removeLike(film1.getId(), user.getId()));

        verify(filmStorage).findById(film1.getId());
        verify(userService, never()).getUserById(anyLong());
        assertThat(filmsArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id %d не найден.", film1.getId()));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserIdNotExistAndRemoveLike() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(film1));
        given(userService.getUserById(anyLong())).willThrow(new UserNotFoundException("Пользователь не найден."));

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                filmsArray[0] = filmService.removeLike(film1.getId(), user.getId()));

        verify(filmStorage).findById(film1.getId());
        verify(userService).getUserById(user.getId());
        assertThat(filmsArray[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден.");
    }

    @Test
    void shouldReturnTopLikedFilms() {
        final List<Film> filmList = List.of(film1, film2);
        given(filmStorage.getAllFilms()).willReturn(filmList);

        film2.addLike(user.getId());
        final List<Film> topLikedFilms = filmService.getTopLikedFilms(filmList.size());

        verify(filmStorage).getAllFilms();
        assertThat(topLikedFilms).isNotNull();
        assertThat(topLikedFilms.size()).isEqualTo(filmList.size());
        assertThat(topLikedFilms).isEqualTo(List.of(film2, film1));
    }

    @Test
    void shouldReturnTopLikedFilmWhenCountIsOne() {
        final List<Film> filmList = List.of(film1, film2);
        given(filmStorage.getAllFilms()).willReturn(filmList);

        film2.addLike(user.getId());
        final List<Film> topLikedFilms = filmService.getTopLikedFilms(1);

        verify(filmStorage).getAllFilms();
        assertThat(topLikedFilms).isNotNull();
        assertThat(topLikedFilms.size()).isEqualTo(1);
        assertThat(topLikedFilms).isEqualTo(List.of(film2));
    }
}