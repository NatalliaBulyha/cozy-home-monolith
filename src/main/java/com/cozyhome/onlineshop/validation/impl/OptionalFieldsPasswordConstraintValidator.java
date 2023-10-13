package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldsPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalFieldsPasswordConstraintValidator implements ConstraintValidator<ValidOptionalFieldsPassword, UserInformationRequest> {
    private final String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";

    @Override
    public void initialize(ValidOptionalFieldsPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserInformationRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()
        && request.getOldPassword() != null && !request.getOldPassword().isEmpty()) {
            return request.getNewPassword().matches(regex) && request.getOldPassword().matches(regex);
        } else if (request.getNewPassword() == null || request.getNewPassword().isEmpty()
                && (request.getOldPassword() == null || request.getOldPassword().isEmpty())) {
            return true;
        } else {
            return false;
        }
    }
}
