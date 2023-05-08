package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validate.FirstCinema;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film implements Comparable<Film> {

    @PositiveOrZero
    private long id;
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
    private int duration;
    private final Set<Long> likes = new HashSet<>();
    private final Set<Genre> genres = new HashSet<>();
    @NotNull(message = "Не указан возрастной рейтинг фильма.")
    private Mpa mpa;

    public void addLike(long id) {
        likes.add(id);
    }

    public boolean removeLike(long id) {
        return likes.remove(id);
    }

    public void clearLikes() {
        likes.clear();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public boolean removeGenre(long id) {
        return genres.remove(id);
    }

    public void clearGenres() {
        genres.clear();
    }

    @Override
    public int compareTo(Film o) {
        return Long.compare(this.id, o.id);
    }
}