package com.bohdansavshak.config;

import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfiguration {

  public static final String TAXI_TRIPS_TOPIC = "tt_topic";

  @Bean
  NewTopic pageViewsTopic() {
    return new NewTopic(TAXI_TRIPS_TOPIC, 1, (short) 1);
  }

  @Bean
  JsonMessageConverter jsonMessageConverter() {
    return new JsonMessageConverter();
  }

  @Bean
  KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
    return new KafkaTemplate<>(
        producerFactory,
        Map.of(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class));
  }
}
