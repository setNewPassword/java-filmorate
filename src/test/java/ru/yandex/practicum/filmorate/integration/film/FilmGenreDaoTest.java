package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreType;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreDaoTest {
    Film film1;
    Film film2;
    final FilmGenreStorage filmGenreStorage;
    final FilmDbStorage filmDbStorage;

    @BeforeEach
    public void createFilms() {
        film1 = Film.builder()
                .name("Titanic")
                .description("American epic romance and disaster film directed by James Cameron.")
                .releaseDate(LocalDate.of(1997, 12, 19))
                .duration(195)
                .mpa(new Mpa(1))
                .build();
        film2 = Film.builder()
                .name("The Shawshank Redemption")
                .description("American drama film directed by Frank Darabont, based on the 1982 Stephen King novella.")
                .releaseDate(LocalDate.of(1994, 9, 23))
                .duration(142)
                .mpa(new Mpa(2))
                .build();
    }

    @Test
    void testSave() {
        final int genreId = new Random().nextInt(GenreType.values().length - 1) + 1;
        final Genre genre = new Genre(genreId);
        film1.getGenres().add(genre);
        final long filmId = filmDbStorage.save(film1).getId();

        filmGenreStorage.save(film1);
        List<Genre> genres = new ArrayList<>(filmGenreStorage.getGenresByFilmId(filmId));

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsAll(film1.getGenres());
    }

    @Test
    void testFindAllById() {
        final Genre genre1 = new Genre(2);
        final Genre genre2 = new Genre(5);
        film1.getGenres().addAll(List.of(genre1, genre2));
        final long filmId = filmDbStorage.save(film1).getId();

        filmGenreStorage.save(film1);
        List<Genre> genres = new ArrayList<>(filmGenreStorage.getGenresByFilmId(filmId));

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsAll(film1.getGenres());
    }

    @Test
    void testDeleteByFilmId() {
        film1.getGenres().addAll(List.of(new Genre(2), new Genre(5)));
        final long filmId = filmDbStorage.save(film1).getId();

        filmGenreStorage.save(film1);
        filmGenreStorage.deleteByFilmId(filmId);
        List<Genre> genres = filmGenreStorage.getGenresByFilmId(filmId);

        assertThat(genres)
                .isNotNull()
                .isEmpty();
    }
}