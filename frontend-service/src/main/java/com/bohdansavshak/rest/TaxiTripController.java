package com.bohdansavshak.rest;

import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.kafka.producer.KafkaProducer;
import com.bohdansavshak.kafka.producer.KafkaProducer.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1")
public class TaxiTripController {

  private final KafkaProducer kafka;

  @PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Response>> kafkaMessage(@RequestBody TaxiTrip taxiTrip) {
    return kafka.send(taxiTrip);
  }

  @GetMapping(path = "/total")
  public Mono<ResponseEntity<TotalResponse>> getTotal(
      @RequestParam("year") Integer year,
      @RequestParam("month") Integer month,
      @RequestParam(value = "day", required = false) Integer day) {

    return null;
  }

  record TotalResponse(BigDecimal total, LocalDate date, String errorMessage) {}
}
