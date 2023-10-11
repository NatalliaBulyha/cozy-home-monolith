package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidFirstNameAndLastName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FirstNameAndLastNameConstraintValidator implements ConstraintValidator<ValidFirstNameAndLastName, String> {
    @Override
    public void initialize(ValidFirstNameAndLastName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return name != null && name.matches("^[a-zA-Z]{2,32}$");
    }
}
