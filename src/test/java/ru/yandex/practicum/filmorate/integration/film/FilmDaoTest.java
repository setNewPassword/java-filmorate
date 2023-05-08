package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDaoTest {
    Film film1;
    Film film2;
    final FilmDbStorage filmStorage;


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
    void testSaveFilm() {
        assertThat(film1.getId()).isZero();
        final Film saved = filmStorage.save(film1);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotZero();
        assertThat(saved.getName()).isEqualTo(film1.getName());
        assertThat(saved.getDescription()).isEqualTo(film1.getDescription());
        assertThat(saved.getReleaseDate()).isEqualTo(film1.getReleaseDate());
        assertThat(saved.getDuration()).isEqualTo(film1.getDuration());
        assertThat(saved.getMpa()).isEqualTo(film1.getMpa());
    }

    @Test
    void testUpdateFilm() {
        final long id = filmStorage.save(film1).getId();
        film2.setId(id);

        final Film saved = filmStorage.save(film2);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getName()).isEqualTo(film2.getName());
        assertThat(saved.getDescription()).isEqualTo(film2.getDescription());
        assertThat(saved.getReleaseDate()).isEqualTo(film2.getReleaseDate());
        assertThat(saved.getDuration()).isEqualTo(film2.getDuration());
        assertThat(saved.getMpa()).isEqualTo(film2.getMpa());
    }

    @Test
    void testGetAllFilms() {
        final Film savedFirst = filmStorage.save(film1);
        final Film savedSecond = filmStorage.save(film2);

        final List<Film> allFilms = filmStorage.getAllFilms();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(2);
        assertTrue(allFilms.containsAll(List.of(savedFirst, savedSecond)));
    }

    @Test
    void testFindById() {
        final long id = filmStorage.save(film1).getId();

        final Optional<Film> returned = filmStorage.findById(id);

        assertThat(returned)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("name", film1.getName())
                                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate())
                                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                                .hasFieldOrPropertyWithValue("mpa", film1.getMpa())
                );
    }

    @Test
    void testFindAllById() {
        final Film savedFirst = filmStorage.save(film1);
        final Film savedSecond = filmStorage.save(film2);

        final Collection<Film> films = filmStorage.findAllById(List.of(savedFirst.getId(), savedSecond.getId()));

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(2);
        assertTrue(films.containsAll(List.of(savedFirst, savedSecond)));
    }

    @Test
    void testExistsById() {
        final Film saved = filmStorage.save(film1);

        assertThat(saved.getId()).isNotEqualTo(0);
        assertTrue(filmStorage.existsById(saved.getId()));
    }
}