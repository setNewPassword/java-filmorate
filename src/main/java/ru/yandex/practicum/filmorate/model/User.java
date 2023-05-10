package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.validate.NameValidator;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
@Builder(toBuilder = true)
public class User {

    @PositiveOrZero
    private long id;
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

    private Set<Long> friends;

    @JsonCreator
    public User(@JsonProperty("id") Long id,
                @JsonProperty("email") String email,
                @JsonProperty("login") String login,
                @JsonProperty("name") String name,
                @JsonProperty("birthday") LocalDate birthday,
                @JsonProperty("friends") Set<Long> friends) {
        this.id = (id == null) ? 0 : id;
        this.email = email;
        this.login = login;
        this.name = NameValidator.validateName(name, login);
        this.birthday = birthday;
        this.friends = (friends == null) ? new HashSet<>() : friends;
    }

    public void addFriendId(long id) {
        friends.add(id);
    }

    public boolean deleteFriendId(long id) {
        return friends.remove(id);
    }

    public List<Long> getFriends() {
        return new ArrayList<>(friends);
    }

    public void clearFriendList() {
        friends.clear();
    }
}