package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.time.LocalDate;

import ru.yandex.practicum.filmorate.validate.NameValidator;

@Data
@Slf4j
@Builder(toBuilder = true)
public class User {

    @PositiveOrZero
    private Integer id;
    @NotBlank(message = "Не указан адрес электронной почты.")
    @Email(message = "Некорректный адрес электронной почты.")
    private String email;
    @NotBlank(message = "Не указан логин")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private String login;
    private String name;
    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;

    @JsonCreator
    public User (@JsonProperty("id") Integer id,
                 @JsonProperty("email") String email,
                 @JsonProperty("login") String login,
                 @JsonProperty("name") String name,
                 @JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = NameValidator.validateName(name, login);
        this.birthday = birthday;
    }
}