package ru.yandex.practicum.filmorate.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class FirstCinemaValidator implements ConstraintValidator<FirstCinema, LocalDate> {

    private static final LocalDate FIRST_CINEMA_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(@NotNull LocalDate date, ConstraintValidatorContext context) {
        return !date.isBefore(FIRST_CINEMA_DATE);
    }
}