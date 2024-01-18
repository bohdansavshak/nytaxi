package com.bohdansavshak.rest;

import static com.bohdansavshak.kafka.producer.KafkaProducer.FAILED_TO_DELIVER_MESSAGE;
import static com.bohdansavshak.kafka.producer.KafkaProducer.MESSAGE_RECEIVED;
import static com.bohdansavshak.rest.TaxiTripController.*;
import static org.assertj.core.api.Assertions.assertThat;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = TaxiTripController.class)
@Import({KafkaProducer.class, RedisRepository.class, SecurityConfiguration.class})
class TaxiTripControllerTest {

  @MockBean RedisRepository redisRepository;
  @MockBean KafkaProducer kafkaProducer;
  @Autowired private WebTestClient webClient;

  @Test
  void message_correctMessage_expected200() {
    // SETUP
    TaxiTrip taxiTrip =
        prepareTaxiTrip(); // <--- This is a helper method that creates a valid TaxiTrip instance

    when(kafkaProducer.send(any(TaxiTrip.class)))
        .thenReturn(Mono.just(ResponseEntity.ok(new KafkaProducer.Response(MESSAGE_RECEIVED))));

    // ACT
    WebTestClient.ResponseSpec response =
        webClient
            .mutateWith(csrf())
            .mutateWith(mockJwt())
            .post()
            .uri("/api/v1/message")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(taxiTrip)
            .exchange();

    // VERIFY
    response.expectStatus().isOk();
    response.expectBody().json("{message:  'Message received'}");
    verify(kafkaProducer, times(1)).send(any(TaxiTrip.class));
  }

  @Test
  void message_passengerCountMinus1_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setPassengerCount(-1);

    String expectedErrorMessage =
        "passengerCount must be positive number or zero";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_passengerCountMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setPassengerCount(null);

    String expectedErrorMessage = "passengerCount is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_tpepPickupDatetimeMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTpepPickupDatetime(null);

    String expectedErrorMessage = "tpepPickupDatetime datetime is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_fareAmountMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setFareAmount(null);

    String expectedErrorMessage = "fareAmount is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_extraMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setExtra(null);

    String expectedErrorMessage = "extra is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_mtaTaxMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setMtaTax(null);

    String expectedErrorMessage = "mtaTax is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_improvementSurchargeMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setImprovementSurcharge(null);

    String expectedErrorMessage = "improvementSurcharge is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_tipAmountMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTipAmount(null);

    String expectedErrorMessage = "tipAmount is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_tollsAmountMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTollsAmount(null);

    String expectedErrorMessage = "tollsAmount is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_totalAmountNegative_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTotalAmount(BigDecimal.valueOf(-1));

    String expectedErrorMessage = "totalAmount must be a positive number";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_totalAmountMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTotalAmount(null);

    String expectedErrorMessage = "totalAmount is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_tripDistanceMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTripDistance(null);

    String expectedErrorMessage = "tripDistance is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_tripDistanceIsNegativeValue_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTripDistance(BigDecimal.valueOf(-1));

    String expectedErrorMessage = "tripDistance must be a positive value.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_puLocationIdMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setPuLocationId(null);

    String expectedErrorMessage = "puLocationId is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_doLocationIdMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setDoLocationId(null);

    String expectedErrorMessage = "doLocationId is mandatory.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  @Test
  void message_tpepDropoffDatetimeMissing_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTpepDropoffDatetime(null);

    String expectedErrorMessage1 = "tpepDropoffDatetime datetime is mandatory.";
    String expectedErrorMessage2 = "tpepDropoffDatetime invalid datatime format.";
    List<String> expectedErrors = List.of(expectedErrorMessage1, expectedErrorMessage2);

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    response.expectStatus().isBadRequest();

    var errorResponses =
        response
            .returnResult(GlobalExceptionHandler.ErrorResponses.class)
            .getResponseBody()
            .blockFirst();
    assertThat(errorResponses.errorMessages()).containsExactlyInAnyOrderElementsOf(expectedErrors);
    assertThat(errorResponses.error()).isEqualTo("Bad Request");
    assertThat(errorResponses.status()).isEqualTo(400);
    assertThat(errorResponses.timestamp())
        .isStrictlyBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
  }

  @Test
  void message_tpepDropoffDatetimeWithWrongFormat_expected400ErrorReturned() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();
    taxiTrip.setTpepDropoffDatetime("12/31/2022 109:00:00 PaM");

    String expectedErrorMessage = "tpepDropoffDatetime invalid datatime format.";

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    verifyBadRequest(response, expectedErrorMessage);
  }

  private void verifyBadRequest(WebTestClient.ResponseSpec response, String expectedErrorMessage) {
    response.expectStatus().isBadRequest();

    var errorResponses =
        response
            .returnResult(GlobalExceptionHandler.ErrorResponses.class)
            .getResponseBody()
            .blockFirst();
    assertThat(errorResponses.errorMessages())
        .containsExactlyInAnyOrderElementsOf(List.of(expectedErrorMessage));
    assertThat(errorResponses.error()).isEqualTo("Bad Request");
    assertThat(errorResponses.status()).isEqualTo(400);
    assertThat(errorResponses.timestamp())
        .isStrictlyBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
  }

  private WebTestClient.ResponseSpec makeRequestPostMessage(TaxiTrip taxiTrip) {
    return webClient
        .mutateWith(mockJwt())
        .mutateWith(csrf())
        .post()
        .uri("/api/v1/message")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(taxiTrip))
        .exchange();
  }

  private TaxiTrip prepareTaxiTrip() {
    TaxiTrip taxiTrip = new TaxiTrip();
    taxiTrip.setTpepPickupDatetime("12/31/2022 08:30:00 PM");
    taxiTrip.setTpepDropoffDatetime("12/31/2022 09:00:00 PM");
    taxiTrip.setPassengerCount(2);
    taxiTrip.setTripDistance(new BigDecimal("15.25"));
    taxiTrip.setPuLocationId(123);
    taxiTrip.setDoLocationId(456);
    taxiTrip.setStoreAndFwdFlag(true);
    taxiTrip.setFareAmount(new BigDecimal("50.00"));
    taxiTrip.setExtra(new BigDecimal("5.00"));
    taxiTrip.setMtaTax(new BigDecimal("2.50"));
    taxiTrip.setImprovementSurcharge(new BigDecimal("1.00"));
    taxiTrip.setTipAmount(new BigDecimal("10.00"));
    taxiTrip.setTollsAmount(new BigDecimal("3.00"));
    taxiTrip.setTotalAmount(new BigDecimal("71.50"));
    taxiTrip.setVendorId(12345L);
    taxiTrip.setRateCodeId(1L);
    taxiTrip.setPaymentTypeId(2L);
    return taxiTrip;
  }

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
  void message_withAccessToken_expect200() {
    // SETUP
    TaxiTrip taxiTrip = prepareTaxiTrip();

    // ACT
    webClient
        .mutateWith(csrf())
        .mutateWith(mockJwt())
        .post()
        .uri("/api/v1/message")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(taxiTrip))
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void message_kafkaFailed_expected500statusReturned() {
    // SETUP
    var taxiTrip = prepareTaxiTrip();

    when(kafkaProducer.send(any()))
        .thenReturn(
            Mono.just(
                ResponseEntity.internalServerError()
                    .body(new KafkaProducer.Response(FAILED_TO_DELIVER_MESSAGE))));

    // ACT
    WebTestClient.ResponseSpec response = makeRequestPostMessage(taxiTrip);

    // VERIFY
    response.expectStatus().is5xxServerError();
    var errorResponses =
        response.returnResult(KafkaProducer.Response.class).getResponseBody().blockFirst();
    assertThat(errorResponses.message()).isEqualTo(FAILED_TO_DELIVER_MESSAGE);
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
  void getTotal_withAccessToken_expect200() {
    // SETUP
    Integer year = 2022;
    Integer month = 12;

    when(redisRepository.getTotalPerMonth(any())).thenReturn(Mono.just(BigDecimal.ONE));

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
        .isOk();
  }

  @Test
  void getTotal_yearMonthProvided_expected200totalForMonthReturned() {
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
  void getTotal_yearMonthDayProvided_expected200totalForDayReturned() {
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

  @Test
  void getTotal_yearMonthDayWithWrongDayValue_expected400ErrorReturned() {
    // SETUP
    int year = 2022;
    int month = 12;
    int day = 100;

    when(redisRepository.getTotalPerDay(any())).thenReturn(Mono.just(BigDecimal.valueOf(30001)));

    // ACT
    var response =
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
            .exchange();

    // VERIFY
    response.expectStatus().isBadRequest();

    var errorResponses =
        response
            .returnResult(GlobalExceptionHandler.ErrorResponses.class)
            .getResponseBody()
            .blockFirst();
    assertThat(errorResponses.errorMessages())
        .hasSize(1)
        .anyMatch(message -> message.contains("Error processing year,month,day value:"));
    assertThat(errorResponses.error()).isEqualTo("Bad Request");
    assertThat(errorResponses.status()).isEqualTo(400);
    assertThat(errorResponses.timestamp())
        .isStrictlyBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
  }

  @Test
  void getTotal_yearMonthDayWithWrongMonthValue_expected400ErrorReturned() {
    // SETUP
    int year = 2022;
    int month = 13;
    int day = 12;

    when(redisRepository.getTotalPerDay(any())).thenReturn(Mono.just(BigDecimal.valueOf(30001)));

    // ACT
    var response =
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
            .exchange();

    // VERIFY
    response.expectStatus().isBadRequest();

    var errorResponses =
        response
            .returnResult(GlobalExceptionHandler.ErrorResponses.class)
            .getResponseBody()
            .blockFirst();
    assertThat(errorResponses.errorMessages())
        .hasSize(1)
        .anyMatch(message -> message.contains("Error processing year,month,day value:"));
    assertThat(errorResponses.error()).isEqualTo("Bad Request");
    assertThat(errorResponses.status()).isEqualTo(400);
    assertThat(errorResponses.timestamp())
        .isStrictlyBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
  }

  @Test
  void getTotal_onlyYearProvided_expected400ErrorReturned() {
    // SETUP
    int year = 2022;

    when(redisRepository.getTotalPerDay(any())).thenReturn(Mono.just(BigDecimal.valueOf(30001)));

    // ACT
    var response =
        webClient
            .mutateWith(mockJwt())
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/v1/total").queryParam("year", year).build())
            .exchange();

    // VERIFY
    response.expectStatus().isBadRequest();
  }

  @Test
  void getTotal_onlyMonthProvided_expected400ErrorReturned() {
    // SETUP
    int month = 12;

    when(redisRepository.getTotalPerDay(any())).thenReturn(Mono.just(BigDecimal.valueOf(30001)));

    // ACT
    var response =
        webClient
            .mutateWith(mockJwt())
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/v1/total").queryParam("month", month).build())
            .exchange();

    // VERIFY
    response.expectStatus().isBadRequest();
  }

  @Test
  void getTotal_noParametersProvided_expected400ErrorReturned() {
    // SETUP
    when(redisRepository.getTotalPerDay(any())).thenReturn(Mono.just(BigDecimal.valueOf(30001)));

    // ACT
    var response =
        webClient
            .mutateWith(mockJwt())
            .get()
            .uri(uriBuilder -> uriBuilder.path("/api/v1/total").build())
            .exchange();

    // VERIFY
    response.expectStatus().isBadRequest();
  }

  @Test
  void getTotal_yearMonthDayProvidedForDayThatDoesntHaveAnyRecords_expected204statusReturned() {
    // SETUP
    int year = 2022;
    int month = 12;
    int day = 12;

    when(redisRepository.getTotalPerDay(any())).thenReturn(Mono.empty());

    // ACT
    var response =
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
            .exchange();

    // VERIFY
    response.expectStatus().isNoContent();
  }

  @Test
  void getTotal_yearMonthProvidedForMonthThatDoesntHaveAnyRecords_expected204statusReturned() {
    // SETUP
    int year = 2022;
    int month = 12;

    when(redisRepository.getTotalPerMonth(any())).thenReturn(Mono.empty());

    // ACT
    var response =
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
            .exchange();

    // VERIFY
    response.expectStatus().isNoContent();
  }
}
