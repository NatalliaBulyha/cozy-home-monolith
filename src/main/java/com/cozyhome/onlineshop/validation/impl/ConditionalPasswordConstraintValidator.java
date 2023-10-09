package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.validation.ValidConditionalPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConditionalPasswordConstraintValidator implements ConstraintValidator<ValidConditionalPassword, UserInformationRequest> {
    @Override
    public void initialize(ValidConditionalPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserInformationRequest userInformationRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (userInformationRequest.getNewPassword() != null && !userInformationRequest.getNewPassword().isEmpty()) {
            return userInformationRequest.getNewPassword()
                    .matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        }

        if (userInformationRequest.getOldPassword() != null && !userInformationRequest.getOldPassword().isEmpty()) {
            return userInformationRequest.getOldPassword()
                    .matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        }

        return true;
    }
}
