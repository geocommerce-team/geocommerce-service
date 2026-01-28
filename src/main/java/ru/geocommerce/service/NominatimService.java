package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.NominatimClient;
import ru.geocommerce.model.GeoRegion;
import ru.geocommerce.extern.dto.NominatimResponse;

@Service
public class NominatimService {
    private final NominatimClient nominatimClient;

    public NominatimService(NominatimClient nominatimClient) {
        this.nominatimClient = nominatimClient;
    }

    public GeoRegion getCity(double latMin, double lonMin,
                             double latMax, double lonMax) {


        NominatimResponse dto = nominatimClient.getCity((latMin + latMax) / 2, (lonMin + lonMax) / 2);

        return getCityBoundingBox(dto);
    }

    private GeoRegion getCityBoundingBox(NominatimResponse dto) {
        if (dto.getBoundingbox() == null || dto.getBoundingbox().size() != 4) return null;

        double minLat = Double.parseDouble(dto.getBoundingbox().get(0));
        double maxLat = Double.parseDouble(dto.getBoundingbox().get(1));
        double minLon = Double.parseDouble(dto.getBoundingbox().get(2));
        double maxLon = Double.parseDouble(dto.getBoundingbox().get(3));

        return new GeoRegion(minLat, minLon, maxLat, maxLon, dto.getPlace_id());
    }
}
