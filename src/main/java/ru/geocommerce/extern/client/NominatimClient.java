package ru.geocommerce.extern.client;

import ru.geocommerce.extern.dto.NominatimResponse;

import java.util.List;

public interface NominatimClient {
    List<NominatimResponse> getCity(double latMin, double lonMin, double latMax, double lonMax);
}
