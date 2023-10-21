package com.bohdansavshak.config;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@WritingConverter
public class LocalDateToStringConverter implements Converter<LocalDate, String> {

  @Override
  public String convert(LocalDate source) {
    return source.toString();
  }
}
