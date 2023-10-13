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
    /*public boolean isValid2(UserInformationRequest userInformationRequest, ConstraintValidatorContext constraintValidatorContext) {
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
    }*/

    @Override
    public boolean isValid(UserInformationRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request.getOldPassword() == null && request.getNewPassword() == null) {
            return true;
        } else if (request.getOldPassword() != null && !request.getOldPassword().isEmpty()
                && request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            return request.getNewPassword().matches(regex) && request.getOldPassword().matches(regex);
        } else {
            return false;
        }
    }
}
