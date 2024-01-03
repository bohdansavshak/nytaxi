package com.bohdansavshak.kafka.producer;

import com.bohdansavshak.model.TaxiTrip;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.bohdansavshak.config.KafkaConfiguration.TAXI_TRIPS_TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

public class KafkaProducerUnitTest {
    private KafkaTemplate<Object, Object> template;
    private KafkaProducer producer;

    @BeforeEach
    void setup() {
        this.template = Mockito.mock(KafkaTemplate.class);
        this.producer = new KafkaProducer(this.template);
    }

    @Test
    public void shouldSendSuccessfully() {
        TaxiTrip taxiTrip = TaxiTrip.builder().build();
        RecordMetadata metadata = Mockito.mock(RecordMetadata.class);
        SendResult<Object, Object> sendResult = new SendResult<>(null, metadata);
        when(template.send(eq(TAXI_TRIPS_TOPIC), any(TaxiTrip.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        Mono<ResponseEntity<KafkaProducer.Response>> sendResponseMono = producer.send(taxiTrip);

        StepVerifier.create(sendResponseMono)
                    .expectNext(ResponseEntity.ok().body(new KafkaProducer.Response("Message received1")))
                    .verifyComplete();
    }

}