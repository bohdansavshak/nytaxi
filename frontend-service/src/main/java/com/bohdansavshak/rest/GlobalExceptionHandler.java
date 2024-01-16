package com.bohdansavshak.rest;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DateTimeException.class)
  public ResponseEntity<ErrorResponses> handleDateTimeException(DateTimeException ex) {
    return ResponseEntity.badRequest()
        .body(
            new ErrorResponses(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                List.of("Error processing year,month,day value: " + ex.getMessage())));
  }

  @ExceptionHandler({InvalidTotalResponseException.class})
  public ResponseEntity<ErrorResponses> handleInvalidTotalResponse(
      InvalidTotalResponseException ex) {
    log.info("Invalid total amount: ", ex);
    return ResponseEntity.badRequest()
        .body(
            new ErrorResponses(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                List.of(ex.getMessage())));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ErrorResponses> handleException(WebExchangeBindException e) {
    var errors =
        e.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());
    log.info("User validation data errors: {}", errors);
    return ResponseEntity.badRequest()
        .body(
            new ErrorResponses(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors));
  }

  record ErrorResponses(
      LocalDateTime timestamp, Integer status, String error, List<String> errorMessages) {}
}
