package ru.geocommerce.extern.client;

import ru.geocommerce.extern.dto.NominatimResponse;

public interface NominatimClient {
    NominatimResponse getCity(double lat, double lon);
}
