package ru.geocommerce.extern.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.geocommerce.extern.client.PopulationClient;
import ru.geocommerce.extern.dto.PopulationResponse;

@Component
public class PopulationWebClient implements PopulationClient {

    private final WebClient webClient;
    private final String baseUrl;

    public PopulationWebClient(WebClient webClient,
                               @Value("${external.population.url:http://population:8083}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public int getPopulation(double latMin, double lonMin, double latMax, double lonMax) {
        PopulationResponse response = webClient
                .get()
                .uri(baseUrl + "/api/population?lat_min={latMin}&lon_min={lonMin}&lat_max={latMax}&lon_max={lonMax}",
                        latMin, lonMin, latMax, lonMax)
                .retrieve()
                .bodyToMono(PopulationResponse.class)
                .block();

        return response != null ? response.getPopulation() : 0;
    }
}