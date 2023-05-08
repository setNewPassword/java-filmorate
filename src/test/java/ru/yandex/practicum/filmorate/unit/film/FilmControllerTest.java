package ru.yandex.practicum.filmorate.unit.film;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)

public class FilmControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    FilmService service;
    Film film1;
    Film film2;
    static Random random = new Random();

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
    void shouldAddFilmAndReturnIt() throws Exception {
        when(service.create(film1)).thenReturn(film1);

        var mvcRequest = post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(film1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(film1.getName())))
                .andExpect(jsonPath("$.description", is(film1.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film1.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film1.getDuration())));
    }

    @Test
    void shouldUpdateFilmAndReturnIt() throws Exception {
        film2.setId(1L);
        when(service.update(film2)).thenReturn(film2);

        var mvcRequest = put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(film2))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is((int) film2.getId())))
                .andExpect(jsonPath("$.name", is(film2.getName())))
                .andExpect(jsonPath("$.description", is(film2.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film2.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film2.getDuration())));
    }

    @Test
    void shouldReturnAllFilms() throws Exception {
        film1.setId(1L);
        film2.setId(2L);
        when(service.getAllFilms()).thenReturn(List.of(film1, film2));

        var mvcRequest = get("/films").accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(film1, film2))))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains((int) film1.getId(), (int) film2.getId())))
                .andExpect(jsonPath("$[*].name", contains(film1.getName(), film2.getName())))
                .andExpect(jsonPath("$[*].description",
                        contains(film1.getDescription(), film2.getDescription())))
                .andExpect(jsonPath("$[*].releaseDate",
                        contains(film1.getReleaseDate().toString(), film2.getReleaseDate().toString())))
                .andExpect(jsonPath("$[*].duration", contains(film1.getDuration(), film2.getDuration())));
    }

    @Test
    public void shouldReturnFilmById() throws Exception {
        when(service.getFilmById(film1.getId())).thenReturn(film1);

        var mvcRequest = get("/films/" + film1.getId());

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(film1)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) film1.getId())))
                .andExpect(jsonPath("$.name", is(film1.getName())))
                .andExpect(jsonPath("$.description", is(film1.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film1.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film1.getDuration())));
    }

    @Test
    void shouldAddLikeAndReturnFilm() throws Exception {
        when(service.addLike(anyLong(), anyLong())).thenReturn(film1);

        var mvcRequest = put(String.format("/films/%d/like/%d", film1.getId(), random.nextLong()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(film1)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) film1.getId())))
                .andExpect(jsonPath("$.name", is(film1.getName())))
                .andExpect(jsonPath("$.description", is(film1.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film1.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film1.getDuration())));
    }

    @Test
    void shouldRemoveLikeAndReturnFilm() throws Exception {
        when(service.removeLike(anyLong(), anyLong())).thenReturn(film1);

        var mvcRequest = delete(String.format("/films/%d/like/%d", film1.getId(), random.nextLong()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(film1)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) film1.getId())))
                .andExpect(jsonPath("$.name", is(film1.getName())))
                .andExpect(jsonPath("$.description", is(film1.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film1.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film1.getDuration())));
    }

    @Test
    void shouldGetTopLikedFilms() throws Exception {
        when(service.getTopLikedFilms(anyInt())).thenReturn(List.of(film1, film2));

        var mvcRequest = get("/films/popular");

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(film1, film2))))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains((int) film1.getId(), (int) film2.getId())))
                .andExpect(jsonPath("$[*].name", contains(film1.getName(), film2.getName())))
                .andExpect(jsonPath("$[*].description",
                        contains(film1.getDescription(), film2.getDescription())))
                .andExpect(jsonPath("$[*].releaseDate",
                        contains(film1.getReleaseDate().toString(), film2.getReleaseDate().toString())))
                .andExpect(jsonPath("$[*].duration",
                        contains(film1.getDuration(), film2.getDuration())));
    }

    @Test
    void shouldThrowHttpMessageNotReadableExceptionWhenBadJson() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(film1);

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON).content("\"id\": 000");

        mvc.perform(mvcRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof HttpMessageNotReadableException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .startsWith("JSON parse error")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Получен некорректный JSON")))
                .andExpect(jsonPath("$.description", Matchers.startsWith("JSON parse error")));

        verify(service, never()).create(any(Film.class));
    }

    @Test
    void shouldThrowMethodArgumentTypeMismatchExceptionWhenBadPathVariable() throws Exception {
        lenient().when(service.getFilmById(anyLong())).thenReturn(film1);

        var mvcRequest = get(String.format("/films/%s", film1.getName()));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentTypeMismatchException))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",
                        is("Параметр 'filmId' со значением 'Titanic' не может быть приведен к типу 'long'")))
                .andExpect(jsonPath("$.description", containsString("Failed to convert " +
                        "value of type 'java.lang.String' to required type 'long'")));

        verify(service, never()).getFilmById(anyLong());
    }

    @Test
    void shouldThrowMethodArgumentNotValidExceptionWhenObjectHasBadField() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(film1);
        film1.setName("");

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(film1));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                                .getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'film' on field 'name'")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("name")))
                .andExpect(jsonPath("$.description", containsString("Не указано название фильма.")));

        verify(service, never()).create(any(Film.class));
    }

    @Test
    void shouldThrowMethodArgumentNotValidExceptionWhenFilmReleaseDateBeforeFirstFilmDate() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(film1);
        film1.setReleaseDate(LocalDate.of(666, 6, 6));

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(film1));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result
                                .getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'film' on field 'releaseDate'")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("releaseDate")))
                .andExpect(jsonPath("$.description",
                        containsString("Дата выпуска фильма не может быть ранее 28 декабря 1895 года.")));

        verify(service, never()).create(any(Film.class));
    }

    @Test
    void shouldThrowHttpRequestMethodNotSupportedExceptionWhenMethodNotAllowed() throws Exception {
        mvc.perform(patch("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(film1)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof HttpRequestMethodNotSupportedException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .matches("^Request method '(POST|PUT|PATCH|DELETE)' not supported$")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", matchesPattern(
                        "^Request method '(POST|PUT|PATCH|DELETE)' not supported$")))
                .andExpect(jsonPath("$.description", matchesPattern("^(POST|PUT|PATCH|DELETE)$")));
    }

}