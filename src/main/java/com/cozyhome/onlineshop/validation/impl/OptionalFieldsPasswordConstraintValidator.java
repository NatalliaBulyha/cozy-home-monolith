package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldsPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalFieldsPasswordConstraintValidator implements ConstraintValidator<ValidOptionalFieldsPassword, UserInformationRequest> {

    @Override
    public void initialize(ValidOptionalFieldsPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserInformationRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request.getOldPassword() == null || request.getNewPassword() == null || request.getPasswordReset() == null) {
            return false;
        } else if (request.getOldPassword().isEmpty() && request.getNewPassword().isEmpty() && request.getPasswordReset().isEmpty()) {
            return true;
        } else if (!request.getOldPassword().isEmpty() && !request.getNewPassword().isEmpty() && !request.getPasswordReset().isEmpty()) {
            String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
            return request.getOldPassword().matches(regex) && request.getNewPassword().matches(regex)
                    && request.getPasswordReset().matches(regex);
        } else {
            return false;
        }
    }
}
