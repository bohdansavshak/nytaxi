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
    private final StudentServiceImpl studentService;

    @GetMapping(path = "/hello")
    public Flux<Student> getAll() {
        Student student = new Student();
        student.setId(UUID.randomUUID().toString());
        student.setName("MyName");
        student.setGender(Student.Gender.MALE);
        student.setGrade(10);
        studentService.create(student);
        return studentService.getAll();
    }

    @PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Response>> kafkaMessage(@RequestBody TaxiTrip taxiTrip) {
        return service.sendToKafka(taxiTrip);
    }

    @GetMapping(path = "/total")
    public Mono<ResponseEntity<TotalResponse>> getTotal(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "day", required = false) Integer day) {

        LocalDate date = getDate(year, month, day);

        Mono<BigDecimal> totalMono = calculateTotal(date);

        return totalMono.flatMap(total -> {
            if (total == null) {
                return Mono.just(badRequest().body(new TotalResponse(null, null, "Total value cannot be null")));
            }

            if (total.compareTo(BigDecimal.ZERO) < 0) {
                return Mono.just(badRequest().body(new TotalResponse(null, null, "Total value cannot be less than zero.")));
            }

            return Mono.just(ok().body(new TotalResponse(total, date, null)));
        }).onErrorResume(ex -> {
            return Mono.just(
                    status(INTERNAL_SERVER_ERROR).body(new TotalResponse(null, null, "An error occurred.")));
        });
    }

    private static LocalDate getDate(int year, int month, Integer day) {
        return day != null ?
                LocalDate.of(year, month, day) :
                LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    }

    private Mono<BigDecimal> calculateTotal(LocalDate date) {
        return Mono.just((BigDecimal) null);
    }

    record TotalResponse(BigDecimal total, LocalDate date, String errorMessage) {
    }

    record Message(String message) {
    }

}
