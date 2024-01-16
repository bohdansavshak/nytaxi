package com.bohdansavshak.kafka.consumer;

import static com.bohdansavshak.config.KafkaConfiguration.TAXI_TRIPS_TOPIC;

import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.repository.PostgresTaxiTripRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class KafkaConsumer {

  private final PostgresTaxiTripRepository postgresTaxiTripRepository;

  @KafkaListener(topics = TAXI_TRIPS_TOPIC, groupId = "tt_topic_group")
  public void onNewTaxiTrip(Message<TaxiTrip> taxiTrip) {
    log.info("----------------");
    TaxiTrip taxiTripPayload = taxiTrip.getPayload();
    log.info("new message received: {}", taxiTripPayload);

    taxiTrip.getHeaders().forEach((s, o) -> log.info("Received message from kafka {} = {}", s, o));
    postgresTaxiTripRepository.save(taxiTripPayload).subscribe();
  }
}
