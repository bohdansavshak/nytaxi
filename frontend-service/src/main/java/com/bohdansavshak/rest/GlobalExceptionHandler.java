package com.bohdansavshak.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler({InvalidTotalResponseException.class})
  public ResponseEntity<ErrorResponse> handleResourceNotFound(InvalidTotalResponseException ex) {
    log.info("Invalid total amount: ", ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
  }

  record ErrorResponse(String errorMessage) {}
}
