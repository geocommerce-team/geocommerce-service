package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.RetailPointsClient;
import ru.geocommerce.extern.dto.RetailPointDto;
import ru.geocommerce.model.GeoRetailPoint;
import ru.geocommerce.repository.GeoRetailPointRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoRetailPointsService {

    private final RetailPointsClient retailPointsClient;
    private final GeoRetailPointRepository geoRetailPointRepository;

    private static final long CACHE_HOURS = 24;

    public GeoRetailPointsService(RetailPointsClient retailPointsClient,
                                  GeoRetailPointRepository geoRetailPointRepository) {
        this.retailPointsClient = retailPointsClient;
        this.geoRetailPointRepository = geoRetailPointRepository;
    }

    public List<GeoRetailPoint> getRetailPoints(String category,
                                                double latMin, double lonMin,
                                                double latMax, double lonMax) {

        LocalDateTime threshold = LocalDateTime.now().minusHours(CACHE_HOURS);

        List<GeoRetailPoint> cached = geoRetailPointRepository
                .findFreshByBoundsAndCategory(category, latMin, lonMax, latMax, lonMax, threshold);

        if (!cached.isEmpty()) {
            return cached;
        }

        List<RetailPointDto> dtos = retailPointsClient.getRetailPoints(category, latMin, lonMin, latMax, lonMax);

        List<GeoRetailPoint> freshPoints = dtos.stream()
                .map(dto -> {
                    String id = generateId(dto.lat(), dto.lon(), category);
                    return new GeoRetailPoint(id, dto.lat(), dto.lon(), dto.name(), category);
                })
                .peek(point -> point.setLastUpdated(LocalDateTime.now()))
                .collect(Collectors.toList());

        geoRetailPointRepository.saveAll(freshPoints);

        return freshPoints;
    }

    private String generateId(double lat, double lon, String category) {
        return (category + "_" + lat + "_" + lon).replaceAll("[^a-zA-Z0-9_.]", "_");
    }
}