package ru.geocommerce.extern.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.geocommerce.extern.client.RentPointsClient;
import ru.geocommerce.extern.dto.RentPointDto;

import java.util.List;

@Component
public class RentPointsWebClient implements RentPointsClient {

    private final WebClient webClient;
    private final String baseUrl;

    public RentPointsWebClient(WebClient webClient,
                               @Value("${external.rent-points.url:http://localhost:8082}") String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<RentPointDto> getRentPoints(double left, double right, double top, double bottom) {
        RentPointDto[] response = webClient
                .get()
                .uri(baseUrl + "/api/rent-points?left={left}&right={right}&top={top}&bottom={bottom}",
                        left, right, top, bottom)
                .retrieve()
                .bodyToMono(RentPointDto[].class)
                .block();

        return response == null ? List.of() : List.of(response);
    }
}