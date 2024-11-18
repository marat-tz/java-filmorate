package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateCheck, LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)) {
            log.error("Дата выхода фильма указана некорректно или не указана вовсе", new NullPointerException());
            return false;
        }
        LocalDate firstDate = LocalDate.of(1895, 12, 28);
        return localDate.isAfter(firstDate) || localDate.isEqual(firstDate);
    }
}
