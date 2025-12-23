package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.PopulationClient;
import ru.geocommerce.model.GeoTraffic;
import ru.geocommerce.repository.GeoTrafficRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeoTrafficService {

    private final PopulationClient populationClient;
    private final GeoTrafficRepository geoTrafficRepository;

    private static final long CACHE_HOURS = 24;

    public GeoTrafficService(PopulationClient populationClient, GeoTrafficRepository geoTrafficRepository) {
        this.populationClient = populationClient;
        this.geoTrafficRepository = geoTrafficRepository;
    }

    public int getPopulation(double latMin, double lonMin, double latMax, double lonMax) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(CACHE_HOURS);

        List<GeoTraffic> cached = geoTrafficRepository
                .findFreshByBounds((latMin + latMax) / 2, (lonMax +  lonMax) / 2, threshold);

        if (!cached.isEmpty()) {
            return cached.getFirst().getCount();
        }

        int dto = populationClient.getPopulation(latMin, lonMin, latMax, lonMax);
        GeoTraffic geoTraffic = new GeoTraffic(generateId((latMin + latMax) / 2, (lonMax +  lonMax) / 2), (latMin + latMax) / 2, (lonMax +  lonMax) / 2, dto);

        List<GeoTraffic> freshPoints = List.of(geoTraffic);

        geoTrafficRepository.saveAll(freshPoints);

        return dto;
    }

    private String generateId(double lat, double lon) {
        return (lat + "_" + lon).replaceAll("[^a-zA-Z0-9_.]", "_");
    }
}