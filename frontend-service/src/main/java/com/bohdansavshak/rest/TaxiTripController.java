package com.bohdansavshak.rest;

import static org.springframework.http.ResponseEntity.ok;

import com.bohdansavshak.kafka.producer.KafkaProducer;
import com.bohdansavshak.kafka.producer.KafkaProducer.Response;
import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.respository.RedisRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
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
      return repository.getTotalPerDay(date).map(total -> ok(new TotalResponse(total, date)));
    } else {
      LocalDate date = YearMonth.of(year, month).atEndOfMonth();
      return repository.getTotalPerMonth(date).map(total -> ok(new TotalResponse(total, date)));
    }
  }

  @GetMapping(path = "/test-code-pipeline-is-working")
  public String hello() {
    return "helloworld";
  }

  record TotalResponse(BigDecimal total, LocalDate date) {
    TotalResponse {
      if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
        throw new InvalidTotalResponseException("Total is null or less than zero for: " + date);
      }
      total = total.setScale(2, RoundingMode.HALF_UP);
    }
  }
}
