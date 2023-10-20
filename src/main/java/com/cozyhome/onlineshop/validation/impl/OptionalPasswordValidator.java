package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.validation.ValidOptionalPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalPasswordValidator implements ConstraintValidator<ValidOptionalPassword, UserInformationRequest> {

    @Override
    public void initialize(ValidOptionalPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserInformationRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request.getOldPassword() == null || request.getNewPassword() == null || request.getRepeatedNewPassword() == null) {
            return false;
        } else if (request.getOldPassword().isEmpty() && request.getNewPassword().isEmpty() && request.getRepeatedNewPassword().isEmpty()) {
            return true;
        } else if (!request.getOldPassword().isEmpty() && !request.getNewPassword().isEmpty() && !request.getRepeatedNewPassword().isEmpty()) {
            String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
            return request.getOldPassword().matches(regex) && request.getNewPassword().matches(regex)
                    && request.getRepeatedNewPassword().matches(regex);
        } else {
            return false;
        }
    }
}
