package ru.geocommerce.extern.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.geocommerce.extern.client.NominatimClient;
import ru.geocommerce.extern.dto.NominatimResponse;

import java.util.List;

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
    public List<NominatimResponse> getCity(double latMin, double lonMin, double latMax, double lonMax) {
        NominatimResponse[] response = webClient
                .get()
                .uri(baseUrl + "/search?format=json&q=city&viewbox=" +
                                lonMin + "," + latMax + "," + lonMax + "," + latMin +
                                "&bounded=1")
                .retrieve()
                .bodyToMono(NominatimResponse[].class)
                .block();
        return response == null ? List.of() : List.of(response);
    }
}
