package ru.yandex.practicum.filmorate.model.film;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Random;

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
                .build();
        film2 = Film.builder()
                .name("The Shawshank Redemption")
                .description("American drama film directed by Frank Darabont, based on the 1982 Stephen King novella.")
                .releaseDate(LocalDate.of(1994, 9, 23))
                .duration(142)
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
}