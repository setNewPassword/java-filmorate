package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Slf4j
@Builder(toBuilder = true)
public class User {

    @PositiveOrZero
    private int id;
    @NotBlank(message = "Не указан адрес электронной почты.")
    @Email(message = "Некорректный адрес электронной почты.")
    private String email;
    @NotNull(message = "Не указан логин")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;

    private String checkName(String name, String login) {
        if (name == null || name.isBlank() ) {
            log.info("В качестве имени использован логин.");
            return login;
        }
        return name;
    }

    @JsonCreator
    public User (@JsonProperty("id") int id,
                 @JsonProperty("email") String email,
                 @JsonProperty("login") String login,
                 @JsonProperty("name") String name,
                 @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = checkName(name, login);
        this.birthday = birthday;
    }
}