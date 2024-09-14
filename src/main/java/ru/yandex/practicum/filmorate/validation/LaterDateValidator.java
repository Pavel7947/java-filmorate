package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class LaterDateValidator implements ConstraintValidator<LaterDate, LocalDate> {
    LocalDate min;

    @Override
    public void initialize(LaterDate constraintAnnotation) {
        this.min = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
        return localDate == null || localDate.isAfter(min);
    }
}


