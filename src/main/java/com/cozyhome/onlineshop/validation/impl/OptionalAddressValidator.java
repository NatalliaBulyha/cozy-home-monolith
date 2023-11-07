package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidOptionalAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalAddressValidator implements ConstraintValidator<ValidOptionalAddress, String> {

    private final String pattern = "^[а-яА-Яa-zA-ZґҐєЄіІїЇ0-9\\s-.]{2,32}+$";
    @Override
    public void initialize(ValidOptionalAddress constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        if (name != null && !name.isEmpty()) {
            return name.matches(pattern);
        }
        return true;
    }
}
