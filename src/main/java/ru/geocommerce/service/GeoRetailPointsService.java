package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.RetailPointsClient;
import ru.geocommerce.extern.dto.RetailPointDto;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.model.GeoRetailPoint;

import java.util.List;

@Service
public class GeoRetailPointsService {

    private final RetailPointsClient retailPointsClient;

    public GeoRetailPointsService(RetailPointsClient retailPointsClient) {
        this.retailPointsClient = retailPointsClient;
    }

    public List<GeoRetailPoint> getRetailPoints(String category, double latMin, double lonMin, double latMax, double lonMax) {
        List<RetailPointDto> dtos = retailPointsClient.getRetailPoints(category, latMin, lonMin, latMax, lonMax);

        return dtos.stream()
                .map(dto -> new GeoRetailPoint("0", dto.lat(), dto.lon(), dto.name(), category))
                .toList();
    }
}