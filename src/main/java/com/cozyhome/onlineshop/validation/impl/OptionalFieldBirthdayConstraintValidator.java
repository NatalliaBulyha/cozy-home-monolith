package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldBirthday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalFieldBirthdayConstraintValidator implements ConstraintValidator<ValidOptionalFieldBirthday, UserInformationRequest> {
    @Override
    public void initialize(ValidOptionalFieldBirthday constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserInformationRequest userInformationRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (userInformationRequest.getBirthday() != null && !userInformationRequest.getBirthday().isEmpty()) {
            return userInformationRequest.getBirthday()
                    .matches("^(?:19|20)\\d{2}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])$");
        }

        return true;
    }
}
