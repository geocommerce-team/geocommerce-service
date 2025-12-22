package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.RentPointsClient;
import ru.geocommerce.extern.dto.RentPointDto;
import ru.geocommerce.model.GeoRentPoint;

import java.util.List;

@Service
public class GeoRentPointsService {

    private final RentPointsClient rentPointsClient;

    public GeoRentPointsService(RentPointsClient rentPointsClient) {
        this.rentPointsClient = rentPointsClient;
    }

    public List<GeoRentPoint> getRentPoints(double left, double right, double top, double bottom) {
        List<RentPointDto> dtos = rentPointsClient.getRentPoints(left, right, top, bottom);

        return dtos.stream()
                .map(dto -> new GeoRentPoint(dto.getId(), dto.getLatitude(), dto.getLongitude(), "", "rent"))
                .toList();
    }
}