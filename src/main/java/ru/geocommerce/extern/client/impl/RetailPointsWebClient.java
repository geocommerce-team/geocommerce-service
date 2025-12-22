package ru.geocommerce.extern.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.geocommerce.extern.client.RetailPointsClient;
import ru.geocommerce.extern.dto.RetailPointDto;

import java.util.List;

@Component
public class RetailPointsWebClient implements RetailPointsClient {

    private final WebClient webClient;
    private final String baseUrl;

    public RetailPointsWebClient(WebClient webClient,
                                 @Value("${external.retail-points.url:http://retail-points:8080}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<RetailPointDto> getRetailPoints(String category, double latMin, double lonMin, double latMax, double lonMax) {
        RetailPointDto[] response = webClient
                .get()
                .uri(baseUrl + "/api/retail-points?category={category}&latMin={latMin}&lonMin={lonMin}&latMax={latMax}&lonMax={lonMax}",
                        category, latMin, lonMin, latMax, lonMax)
                .retrieve()
                .bodyToMono(RetailPointDto[].class)
                .block();

        return response == null ? List.of() : List.of(response);
    }
}