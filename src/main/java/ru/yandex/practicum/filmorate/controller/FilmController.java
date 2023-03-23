package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @GetMapping
    public ResponseEntity<List<Film>> returnAllFilms() {
        return ResponseEntity.ok(filmService.getAllFilms());
    }

    @PostMapping
    public ResponseEntity<Film> addNewFilm(@RequestBody @Valid Film film) {
        return new ResponseEntity<>(filmService.create(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid Film film) {
        return ResponseEntity.ok(filmService.update(film));
    }
}