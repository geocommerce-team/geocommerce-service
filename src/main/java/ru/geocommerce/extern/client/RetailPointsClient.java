package ru.geocommerce.extern.client;

import ru.geocommerce.extern.dto.RetailPointDto;
import java.util.List;

public interface RetailPointsClient {
    List<RetailPointDto> getRetailPoints(String category, double latMin, double lonMin, double latMax, double lonMax);
}