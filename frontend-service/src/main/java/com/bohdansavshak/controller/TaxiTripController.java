package com.bohdansavshak.controller;

import com.bohdansavshak.model.Student;
import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.service.StudentServiceImpl;
import com.bohdansavshak.service.TaxiTripService;
import com.bohdansavshak.service.TaxiTripService.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1")
public class TaxiTripController {

    private final TaxiTripService service;

    @PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Response>> kafkaMessage(@RequestBody TaxiTrip taxiTrip) {
        return service.sendToKafka(taxiTrip);
    }

    @GetMapping(path = "/total")
    public Mono<ResponseEntity<TotalResponse>> getTotal(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "day", required = false) Integer day) {

        return null;
    }

    record TotalResponse(BigDecimal total, LocalDate date, String errorMessage) {
    }

}
