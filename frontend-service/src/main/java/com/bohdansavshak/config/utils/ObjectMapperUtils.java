package com.bohdansavshak.config.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class ObjectMapperUtils {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static <T> T objectMapper(Object obj, Class<T> contentClass) {
    return mapper.convertValue(obj, contentClass);
  }
}
