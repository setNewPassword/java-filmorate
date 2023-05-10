package ru.yandex.practicum.filmorate.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FirstCinemaValidator.class)
public @interface FirstCinema {
    String message() default "Невозможно добавить фильм в приложение. Дата релиза указана не верно.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}