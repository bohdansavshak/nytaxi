package com.bohdansavshak.service;

import com.bohdansavshak.model.TaxiTrip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.bohdansavshak.config.KafkaConfiguration.TAXI_TRIPS_TOPIC;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaxiTripService {

    private final KafkaTemplate<Object, Object> template;

    public Mono<ResponseEntity<Response>> sendToKafka(TaxiTrip taxiTrip) {
        log.info("taxi trip: {}", taxiTrip);
        Mono<SendResult<Object, Object>> sendResultMono = Mono.fromFuture(template.send(TAXI_TRIPS_TOPIC, taxiTrip));

        return sendResultMono.map(result -> {
            // Message sent successfully
            System.out.println("Message sent successfully: " + result.getRecordMetadata().toString());
            return ResponseEntity.ok().body(new Response("Message received"));
        }).onErrorResume(error -> {
            // Error handling for failed message
            System.err.println("Error sending message: " + error.getMessage());
            return Mono.just(ResponseEntity.internalServerError().body(new Response("Failed to deliver message to kafka, Please re-try later")));
        });
    }

    public record Response(String message) {}

}
