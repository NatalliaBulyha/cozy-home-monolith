package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidOptionalBoolean;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalBooleanValidator implements ConstraintValidator<ValidOptionalBoolean, String> {
    private final String pattern = "^(?i)(true|false)$";
    @Override
    public void initialize(ValidOptionalBoolean constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        if (value.isEmpty()) {
            return true;
        }

        return value.matches(pattern);
    }
}
