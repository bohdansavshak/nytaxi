package com.bohdansavshak.kafka.producer;

import static com.bohdansavshak.config.KafkaConfiguration.TAXI_TRIPS_TOPIC;

import com.bohdansavshak.model.TaxiTrip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class KafkaProducer {

  private final KafkaTemplate<Object, Object> template;

  public Mono<ResponseEntity<Response>> send(TaxiTrip taxiTrip) {
    log.info("taxi trip: {}", taxiTrip);
    Mono<SendResult<Object, Object>> sendResultMono =
        Mono.fromFuture(template.send(TAXI_TRIPS_TOPIC, taxiTrip));
    log.info("Test if Mono.fromFuture is blocking");

    return sendResultMono
        .map(
            result -> {
              log.info("Message sent successfully: {}", result.getRecordMetadata());
              return ResponseEntity.ok().body(new Response("Message received"));
            })
        .onErrorResume(
            error -> {
              log.error("Error sending message: {}", error.getMessage());
              return Mono.just(
                  ResponseEntity.internalServerError()
                      .body(
                          new Response("Failed to deliver message to kafka, Please re-try later")));
            });
  }

  public record Response(String message) {}
}
