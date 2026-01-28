package ru.geocommerce.extern.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.geocommerce.extern.client.NominatimClient;
import ru.geocommerce.extern.dto.NominatimResponse;

@Component
public class NominatimWebClient implements NominatimClient {
    private final WebClient webClient;
    private final String baseUrl;

    public NominatimWebClient(WebClient webClient,
                               @Value("${external.nominatim.url:https://nominatim.openstreetmap.org}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public NominatimResponse getCity(double lat, double lon) {

        return webClient
                .get()
                .uri(baseUrl + "/reverse?format=json&lat="
                                + lat + "&lon=" + lon + "&zoom=10&addressdetails=1")
                .retrieve()
                .bodyToMono(NominatimResponse.class)
                .block();
    }
}
