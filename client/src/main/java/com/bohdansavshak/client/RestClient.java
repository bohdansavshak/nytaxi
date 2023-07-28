package com.bohdansavshak.client;

import com.bohdansavshak.model.TaxiTrip;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RestClient {

    private final WebClient client;

    // Spring Boot auto-configures a `WebClient.Builder` instance with nice defaults and customizations.
    // We can use it to create a dedicated `WebClient` for our component.
    public RestClient(WebClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:8080").build();
    }

    public Mono<TaxiTrip> sendMessage(TaxiTrip taxiTrip) {
        return this.client.post()
                .uri("/api/v1/r2dbc/message")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(TaxiTrip.class)
                .map(e -> e);
    }

}