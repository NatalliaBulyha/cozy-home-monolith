package com.cozyhome.onlineshop.validation;

import com.cozyhome.onlineshop.validation.impl.HouseValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = HouseValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
public @interface ValidHouse {
    String message() default "Invalid house number. House number must be numbers, can contains characters / or - and letters.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
