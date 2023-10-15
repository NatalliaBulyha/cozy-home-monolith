package com.cozyhome.onlineshop.validation.impl;

import com.cozyhome.onlineshop.validation.ValidColorHex;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ColorHexValidator implements ConstraintValidator<ValidColorHex, String> {

	private final String emailPattern = "^#[A-Za-z0-9]{3,8}+$";
	
	@Override
	public void initialize(ValidColorHex constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String colorHex, ConstraintValidatorContext constraintValidatorContext) {
		if (colorHex == null) {
			return false;
		}		
		return colorHex.matches(emailPattern);
	}
}
