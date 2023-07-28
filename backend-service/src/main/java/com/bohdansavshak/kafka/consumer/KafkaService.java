package com.bohdansavshak.kafka.consumer;

import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.repository.TaxiTripRepository;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.bohdansavshak.config.KafkaConfiguration.TAXI_TRIPS_TOPIC;

@AllArgsConstructor
@Service
public class KafkaService {

    private final TaxiTripRepository taxiTripRepository;

    @KafkaListener(topics = TAXI_TRIPS_TOPIC, groupId = "tt_topic_group")
    public void onNewTaxiTrip(Message<TaxiTrip> taxiTrip) {
        System.out.println("______________");
        TaxiTrip taxiTripPayload = taxiTrip.getPayload();
        System.out.println("new message received" + taxiTripPayload);
        taxiTrip.getHeaders().forEach((s, o) -> System.out.println("Received message from kafka " + s + "=" + o));
        taxiTripRepository.save(taxiTripPayload);
    }
}
