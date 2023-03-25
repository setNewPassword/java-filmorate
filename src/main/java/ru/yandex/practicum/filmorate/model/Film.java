package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validate.FirstCinema;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {

    @PositiveOrZero
    private Integer id;
    @NotBlank(message = "Не указано название фильма.")
    private String name;
    @NotNull(message = "Не указано описание фильма.")
    @Size(min = 1, max = 200, message = "Максимальный размер описания фильма — 200 символов.")
    private String description;
    @NotNull(message = "Не указана дата выпуска фильма.")
    @FirstCinema(message = "Дата выпуска фильма не может быть ранее 28 декабря 1895 года.")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Продолжительность фильма указана не верно.")
    @Positive
    private long duration;
}