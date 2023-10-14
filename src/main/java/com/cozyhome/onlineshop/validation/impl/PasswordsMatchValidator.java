package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.dto.user.PasswordChangeRequest;
import com.cozyhome.onlineshop.validation.ValidPasswordsMatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordsMatchValidator implements ConstraintValidator<ValidPasswordsMatch, PasswordChangeRequest> {
	
	@Override
	public void initialize(ValidPasswordsMatch constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
		log.info("[ON initialize] :: {} is initialized", constraintAnnotation);
	}

	@Override
	public boolean isValid(PasswordChangeRequest request, ConstraintValidatorContext constraintValidatorContext) {

		return request.getNewPassword().equals(request.getRepeatedNewPassword());
	}
}
