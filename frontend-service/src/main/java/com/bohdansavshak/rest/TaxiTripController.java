package com.bohdansavshak.rest;

import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.kafka.producer.KafkaProducer;
import com.bohdansavshak.kafka.producer.KafkaProducer.Response;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.bohdansavshak.respository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1")
public class TaxiTripController {

  public static final int FIRST_DAY_OF_MONTH = 1;
  private final KafkaProducer kafka;
  private final RedisRepository repository;

  @PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Response>> kafkaMessage(@RequestBody TaxiTrip taxiTrip) {
    return kafka.send(taxiTrip);
  }

  @GetMapping(path = "/total")
  public Mono<ResponseEntity<TotalResponse>> getTotal(
      @RequestParam("year") Integer year,
      @RequestParam("month") Integer month,
      @RequestParam(value = "day", required = false) Integer day) {

    if (day != null) {
      LocalDate date = LocalDate.of(year, month, day);
      return repository.getTotalPerDay(date).map(total -> ok(new TotalResponse(total, date, null)));
    } else {
      LocalDate date = LocalDate.of(year, month, FIRST_DAY_OF_MONTH);
      return repository.getTotalPerMonth(date).map(total -> ok(new TotalResponse(total, date, null)));
    }
  }

  record TotalResponse(BigDecimal total, LocalDate date, String errorMessage) {}
}
