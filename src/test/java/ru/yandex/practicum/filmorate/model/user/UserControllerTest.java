package ru.yandex.practicum.filmorate.model.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import java.time.LocalDate;
import java.util.List;

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

    @BeforeEach
    public void createUsers() {
        user1 = User.builder()
                .email("dee.irk@gmail.com")
                .login("dee")
                .name("Дмитрий")
                .birthday(LocalDate.of(1982, 6, 6))
                .build();
        user2 = User.builder()
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
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.email", is("dee.irk@gmail.com")))
                .andExpect(jsonPath("$.login", is("dee")))
                .andExpect(jsonPath("$.name", is("Дмитрий")))
                .andExpect(jsonPath("$.birthday", is("1982-06-06")));
    }

    @Test
    void shouldUpdateUserAndReturnIt() throws Exception {
        user2.setId(1);
        when(service.update(any(User.class))).thenReturn(user2);

        var mvcRequest = put("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user2))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("volozh@yandex.ru")))
                .andExpect(jsonPath("$.login", is("volozh")))
                .andExpect(jsonPath("$.name", is("Аркадий")))
                .andExpect(jsonPath("$.birthday", is("1964-02-11")));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        user1.setId(1);
        user2.setId(2);
        when(service.getAllUsers()).thenReturn(List.of(user1, user2));

        var mvcRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(user1, user2)))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", contains("dee.irk@gmail.com", "volozh@yandex.ru")))
                .andExpect(jsonPath("$[*].login", contains("dee", "volozh")))
                .andExpect(jsonPath("$[*].name", contains("Дмитрий", "Аркадий")))
                .andExpect(jsonPath("$[*].birthday", contains("1982-06-06", "1964-02-11")))
                .andExpect(jsonPath("$[*].id", contains(1, 2)));
    }
}