package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidOptionalFieldBirthday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalFieldBirthdayConstraintValidator implements ConstraintValidator<ValidOptionalFieldBirthday, String> {
    @Override
    public void initialize(ValidOptionalFieldBirthday constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String birthday, ConstraintValidatorContext constraintValidatorContext) {
        if (birthday != null && !birthday.isEmpty()) {
            return birthday.matches("^(?:19|20)\\d{2}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])$");
        }

        return true;
    }
}
