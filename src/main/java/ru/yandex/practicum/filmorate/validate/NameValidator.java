package ru.yandex.practicum.filmorate.validate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameValidator {
    public static String validateName(String name, String login) {
        if (name == null || name.isBlank() ) {
            log.info("В качестве имени использован логин.");
            return login;
        }
        return name;
    }
}
