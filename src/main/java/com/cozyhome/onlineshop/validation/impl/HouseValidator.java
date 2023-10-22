package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidHouse;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HouseValidator implements ConstraintValidator<ValidHouse, String> {

    private final String housePattern = "^[а-яА-Яa-zA-ZґҐєЄіІїЇ0-9/-]{1,14}$";
    @Override
    public void initialize(ValidHouse constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String house, ConstraintValidatorContext constraintValidatorContext) {
        return house.matches(housePattern);
    }
}
