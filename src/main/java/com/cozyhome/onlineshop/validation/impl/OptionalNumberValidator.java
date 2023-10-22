package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidOptionalNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalNumberValidator implements ConstraintValidator<ValidOptionalNumber, String> {
    private final String pattern = "^[1-9]\\d*$";
    @Override
    public void initialize(ValidOptionalNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String number, ConstraintValidatorContext constraintValidatorContext) {
        if (number == null) {
            return false;
        }
        if (number.isEmpty()) {
            return true;
        }

        return number.matches(pattern);
    }
}
