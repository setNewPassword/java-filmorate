package ru.yandex.practicum.filmorate.unit.user;

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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)

public class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService service;
    User user1;
    User user2;
    static Random random = new Random();

    @BeforeEach
    public void createUsers() {
        user1 = User.builder()
                .id((long) random.nextInt(97))
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
        user2 = User.builder()
                .id(user1.getId() + 1)
                .email("volozh@yandex.ru")
                .login("volozh")
                .name("Аркадий")
                .birthday(LocalDate.of(1964, 2, 11))
                .build();
    }

    @Test
    void shouldAddUserAndReturnIt() throws Exception {
        when(service.create(any(User.class))).thenReturn(user1);

        var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.login", is(user1.getLogin())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.birthday", is(user1.getBirthday().toString())));
    }

    @Test
    void shouldUpdateUserAndReturnIt() throws Exception {
        when(service.update(any(User.class))).thenReturn(user2);

        var mvcRequest = put("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user2))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is((int) user2.getId())))
                .andExpect(jsonPath("$.email", is(user2.getEmail())))
                .andExpect(jsonPath("$.login", is(user2.getLogin())))
                .andExpect(jsonPath("$.name", is(user2.getName())))
                .andExpect(jsonPath("$.birthday", is(user2.getBirthday().toString())));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(service.getAllUsers()).thenReturn(List.of(user1, user2));

        var mvcRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(user1, user2)))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", contains(user1.getEmail(), user2.getEmail())))
                .andExpect(jsonPath("$[*].login", contains(user1.getLogin(), user2.getLogin())))
                .andExpect(jsonPath("$[*].name", contains(user1.getName(), user2.getName())))
                .andExpect(jsonPath("$[*].birthday",
                        contains(user1.getBirthday().toString(), user2.getBirthday().toString())))
                .andExpect(jsonPath("$[*].id", contains((int) user1.getId(), (int) user2.getId())));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        Long id = user1.getId();
        when(service.getUserById(id)).thenReturn(user1);

        final var mvcRequest = get("/users/" + id);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user1)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) user1.getId())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.login", is(user1.getLogin())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.birthday", is(user1.getBirthday().toString())));
    }

    @Test
    void shouldAddFriendAndReturnUser() throws Exception {
        when(service.addFriend(user1.getId(), user2.getId())).thenReturn(user1);

        final var mvcRequest = put(String.format("/users/%d/friends/%d", user1.getId(), user2.getId()));

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user1)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) user1.getId())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.login", is(user1.getLogin())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.birthday", is(user1.getBirthday().toString())));
    }

    @Test
    void shouldRemoveFriendAndReturnUser() throws Exception {
        when(service.removeFriend(anyLong(), anyLong())).thenReturn(user1);

        final var mvcRequest = delete(String.format("/users/%d/friends/%d", user1.getId(), user2.getId()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user1)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) user1.getId())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())))
                .andExpect(jsonPath("$.login", is(user1.getLogin())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.birthday", is(user1.getBirthday().toString())));
    }

    @Test
    void shouldGetListOfFriends() throws Exception {
        final List<User> friends = List.of(user2);
        when(service.getUsersFriends(user1.getId())).thenReturn(friends);

        var mvcRequest = get(String.format("/users/%d/friends", user1.getId()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(friends)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(friends.size())))
                .andExpect(jsonPath("$[0].id", is((int) user2.getId())))
                .andExpect(jsonPath("$[0].email", is(user2.getEmail())))
                .andExpect(jsonPath("$[0].login", is(user2.getLogin())))
                .andExpect(jsonPath("$[0].name", is(user2.getName())))
                .andExpect(jsonPath("$[0].birthday", is(user2.getBirthday().toString())));
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        User commonFriend = User
                .builder()
                .id(user2.getId() + 1)
                .email("brin@gmail.com")
                .name("Сергей")
                .login("googolplex")
                .birthday(LocalDate.of(1973, 8, 21))
                .build();

        when(service.getCommonFriends(user1.getId(), user2.getId()))
                .thenReturn(List.of(commonFriend));

        var mvcRequest = get(String.format("/users/%d/friends/common/%d", user1.getId(), user2.getId()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(commonFriend))))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) commonFriend.getId())))
                .andExpect(jsonPath("$[0].email", is(commonFriend.getEmail())))
                .andExpect(jsonPath("$[0].login", is(commonFriend.getLogin())))
                .andExpect(jsonPath("$[0].name", is(commonFriend.getName())))
                .andExpect(jsonPath("$[0].birthday", is(commonFriend.getBirthday().toString())));
    }

    @Test
    void shouldThrowHttpMessageNotReadableExceptionWhenBadJson() throws Exception {
        lenient().when(service.create(any(User.class))).thenReturn(user1);

        var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("\"id\": 000");

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof HttpMessageNotReadableException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .startsWith("JSON parse error")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Получен некорректный JSON")))
                .andExpect(jsonPath("$.description", Matchers.startsWith("JSON parse error")));

        verify(service, never()).create(any(User.class));
    }

    @Test
    void shouldThrowMethodArgumentTypeMismatchExceptionWhenBadPathVariable() throws Exception {
        lenient().when(service.update(any(User.class))).thenReturn(user1);

        var mvcRequest = get(String.format("/users/%s", user1.getEmail()));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Failed to convert value of type")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString(String.format(
                        "Параметр 'userId' со значением '%s' не может быть приведен к типу 'long'", user1.getEmail()))))
                .andExpect(jsonPath("$.description", containsString("Failed to convert value " +
                        "of type 'java.lang.String' to required type 'long'")));

        verify(service, never()).update(any(User.class));
    }

    @Test
    void shouldThrowMethodArgumentNotValidExceptionWhenObjectHasBadField() throws Exception {
        lenient().when(service.create(any(User.class))).thenReturn(user1);

        user1.setLogin("Дмитрий Евтюхин");
        var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user1));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage()
                        .contains("Field error in object 'user' on field 'login'")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("login")))
                .andExpect(jsonPath("$.description",
                        containsString("Логин содержит пробелы")));

        verify(service, never()).create(any(User.class));
    }

    @Test
    void shouldThrowHttpRequestMethodNotSupportedExceptionWhenMethodNotAllowed() throws Exception {
        mvc.perform(patch("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user1)))
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