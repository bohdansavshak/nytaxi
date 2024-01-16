package com.bohdansavshak.rest.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormat, String> {

  private String pattern;

  @Override
  public void initialize(DateTimeFormat annotation) {
    this.pattern = annotation.pattern();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      if (value == null) {
        return false;
      }
      LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }
}
