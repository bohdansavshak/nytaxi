package com.bohdansavshak.rest.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateTimeFormatValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateTimeFormat {

  String message() default "Invalid date time format.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String pattern();
}
