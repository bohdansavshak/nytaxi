package com.bohdansavshak.rest;

import static com.bohdansavshak.rest.TaxiTripController.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import com.bohdansavshak.config.SecurityConfiguration;
import com.bohdansavshak.kafka.producer.KafkaProducer;
import com.bohdansavshak.model.TaxiTrip;
import com.bohdansavshak.respository.RedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = TaxiTripController.class)
@Import({KafkaProducer.class, RedisRepository.class, SecurityConfiguration.class})
class TaxiTripControllerTest {

  @MockBean RedisRepository redisRepository;
  @MockBean KafkaProducer kafkaProducer;
  @Autowired private WebTestClient webClient;

  @Test
  void message_withoutAccessToken_expect401() throws JsonProcessingException {
    // SETUP
    TaxiTrip taxiTrip = new TaxiTrip();
    taxiTrip.setTpepPickupDatetime("12/31/2022 08:30:00 PM");
    taxiTrip.setTpepDropoffDatetime("12/31/2022 09:00:00 PM");

    String taxiTripJson = new ObjectMapper().writeValueAsString(taxiTrip);

    // ACT
    webClient
        .mutateWith(csrf())
        .post()
        .uri("api/v1/message")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(taxiTripJson), String.class)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void getTotal_withoutAccessToken_expect401() {
    // SETUP
    Integer year = 2022;
    Integer month = 12;

    // ACT
    webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/v1/total")
                    .queryParam("year", year)
                    .queryParam("month", month)
                    .build())
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void getTotal_yearMonthProvided_totalForMonthReturned() {
    // SETUP
    int year = 2022;
    int month = 12;
    var date = YearMonth.of(year, month).atEndOfMonth();
    var expectedResponse = new TotalResponse(BigDecimal.valueOf(30000), date);

    when(redisRepository.getTotalPerMonth(date)).thenReturn(Mono.just(BigDecimal.valueOf(30000)));

    // ACT
    webClient
        .mutateWith(mockJwt())
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/v1/total")
                    .queryParam("year", year)
                    .queryParam("month", month)
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(TotalResponse.class)
        .isEqualTo(expectedResponse);

    // VERIFY
    verify(redisRepository, times(1)).getTotalPerMonth(date);
  }

  @Test
  void getTotal_yearMonthDayProvided_totalForDayReturned() {
    // SETUP
    int year = 2022;
    int month = 12;
    int day = 12;
    var date = LocalDate.of(year, month, day);
    var expectedResponse = new TotalResponse(BigDecimal.valueOf(30001), date);

    when(redisRepository.getTotalPerDay(date)).thenReturn(Mono.just(BigDecimal.valueOf(30001)));

    // ACT
    webClient
        .mutateWith(mockJwt())
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/v1/total")
                    .queryParam("year", year)
                    .queryParam("month", month)
                    .queryParam("day", day)
                    .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(TotalResponse.class)
        .isEqualTo(expectedResponse);

    // VERIFY
    verify(redisRepository, times(1)).getTotalPerDay(date);
  }
}
