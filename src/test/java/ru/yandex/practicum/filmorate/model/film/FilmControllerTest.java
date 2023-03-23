package ru.yandex.practicum.filmorate.model.film;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

import static org.mockito.ArgumentMatchers.any;
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
        when(service.create(any(Film.class))).thenReturn(film1);

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(film1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.name", is("Titanic")))
                .andExpect(jsonPath("$.description",
                        is("American epic romance and disaster film directed by James Cameron.")))
                .andExpect(jsonPath("$.releaseDate", is("1997-12-19")))
                .andExpect(jsonPath("$.duration", is(195)));
    }

    @Test
    void shouldUpdateFilmAndReturnIt() throws Exception {
        film2.setId(1);
        when(service.update(any(Film.class))).thenReturn(film2);

        var mvcRequest = put("/films").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(film2))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("The Shawshank Redemption")))
                .andExpect(jsonPath("$.description",
                        is("American drama film directed by Frank Darabont, based on the 1982 Stephen King novella.")))
                .andExpect(jsonPath("$.releaseDate", is("1994-09-23")))
                .andExpect(jsonPath("$.duration", is(142)));
    }

    @Test
    void shouldReturnAllFilms() throws Exception {
        film1.setId(1);
        film2.setId(2);
        when(service.getAllFilms()).thenReturn(List.of(film1, film2));

        var mvcRequest = get("/films").accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(film1, film2))))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains(1, 2)))
                .andExpect(jsonPath("$[*].name", contains("Titanic", "The Shawshank Redemption")))
                .andExpect(jsonPath("$[*].description",
                        contains("American epic romance and disaster film directed by James Cameron.",
                                "American drama film directed by Frank Darabont, based on the 1982 Stephen King novella.")))
                .andExpect(jsonPath("$[*].releaseDate", contains("1997-12-19", "1994-09-23")))
                .andExpect(jsonPath("$[*].duration", contains(195, 142)));
    }

}
