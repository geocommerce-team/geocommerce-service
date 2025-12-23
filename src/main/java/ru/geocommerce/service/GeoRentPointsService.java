package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.RentPointsClient;
import ru.geocommerce.extern.dto.RentPointDto;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.repository.GeoRentPointRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoRentPointsService {

    private final RentPointsClient rentPointsClient;
    private final GeoRentPointRepository geoRentPointRepository;

    private final long CACHE_HOURS = 24;

    public GeoRentPointsService(RentPointsClient rentPointsClient,
                                GeoRentPointRepository geoRentPointRepository) {
        this.rentPointsClient = rentPointsClient;
        this.geoRentPointRepository = geoRentPointRepository;
    }

    public List<GeoRentPoint> getRentPoints(double left, double right, double top, double bottom) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(CACHE_HOURS);

        List<GeoRentPoint> cached = geoRentPointRepository.findFreshByBounds(left, right, top, bottom, threshold);
        if (!cached.isEmpty()) {
            return cached;
        }

        List<RentPointDto> dtos = rentPointsClient.getRentPoints(left, right, top, bottom);

        List<GeoRentPoint> freshPoints = dtos.stream()
                .map(dto -> new GeoRentPoint(
                        dto.getId(),
                        dto.getLatitude(),
                        dto.getLongitude(),
                        "",
                        "rent"
                ))
                .peek(point -> point.setLastUpdated(LocalDateTime.now()))
                .collect(Collectors.toList());

        geoRentPointRepository.saveAll(freshPoints);

        return freshPoints;
    }
}