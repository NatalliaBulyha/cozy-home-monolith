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
    public boolean isValid(UserInformationRequest userInformationRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (userInformationRequest.getNewPassword() != null && !userInformationRequest.getNewPassword().isEmpty()
        && userInformationRequest.getOldPassword() != null && !userInformationRequest.getOldPassword().isEmpty()) {
            return userInformationRequest.getNewPassword()
                    .matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
                    && userInformationRequest.getOldPassword()
                    .matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        } else if (userInformationRequest.getNewPassword() != null && !userInformationRequest.getNewPassword().isEmpty()
                && (userInformationRequest.getOldPassword() == null || userInformationRequest.getOldPassword().isEmpty())) {
            return false;
        } else if ((userInformationRequest.getNewPassword() == null || userInformationRequest.getNewPassword().isEmpty())
                && userInformationRequest.getOldPassword() != null && !userInformationRequest.getOldPassword().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
