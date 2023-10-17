package com.cozyhome.onlineshop.validation;

import com.cozyhome.onlineshop.validation.impl.FieldsValueMatchValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface ValidFieldsValueMatch {
    String message() default "Fields values don't match!";

    String field();

    String fieldMatch();

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ValidFieldsValueMatch[] value();
    }
}
