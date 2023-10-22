package com.cozyhome.onlineshop.validation;

import com.cozyhome.onlineshop.validation.impl.OptionalBooleanValidator;
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
@Constraint(validatedBy = OptionalBooleanValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE, PARAMETER })
@Retention(RUNTIME)
public @interface ValidOptionalBoolean {
    String message() default "Invalid boolean field. Optional field, must be not null and contains true or false.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
