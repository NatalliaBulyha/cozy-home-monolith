package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidOptionalName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalNameValidator implements ConstraintValidator<ValidOptionalName, String> {

    private final String pattern = "^[а-яА-Яa-zA-ZґҐєЄіІїЇ0-9\\s-.]{2,32}?$";
    @Override
    public void initialize(ValidOptionalName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String region, ConstraintValidatorContext constraintValidatorContext) {
        if (region != null && !region.isEmpty()) {
            return region.matches(pattern);
        }
        return true;
    }
}
