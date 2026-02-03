package ru.geocommerce.extern.client;

import ru.geocommerce.extern.dto.RentPointDto;
import java.util.List;

public interface RentPointsClient {
    List<RentPointDto> getRentPoints(double latMin, double lonMin, double latMax, double lonMax);
}