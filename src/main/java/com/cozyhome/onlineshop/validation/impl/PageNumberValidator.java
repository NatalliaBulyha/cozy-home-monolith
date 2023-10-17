package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidPageNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageNumberValidator implements ConstraintValidator<ValidPageNumber, Integer> {
    @Override
    public void initialize(ValidPageNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer page, ConstraintValidatorContext constraintValidatorContext) {
        return page.toString().matches("[0-9]+");
    }
}
