package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.NominatimClient;
import ru.geocommerce.model.GeoRegion;
import ru.geocommerce.extern.dto.NominatimResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class NominatimService {
    private final NominatimClient nominatimClient;

    public NominatimService(NominatimClient nominatimClient) {
        this.nominatimClient = nominatimClient;
    }

    public List<GeoRegion> getCity(double latMin, double lonMin,
                             double latMax, double lonMax) {


        List<NominatimResponse> dto = nominatimClient.getCity(latMin, lonMin, latMax, lonMax);

        List<GeoRegion> regions = new ArrayList<>();
        for (NominatimResponse nominatimResponse : dto) {
            regions.add(getCityBoundingBox(nominatimResponse));
        }

        return regions;
    }

    private GeoRegion getCityBoundingBox(NominatimResponse dto) {
        if (dto.getBoundingbox() == null || dto.getBoundingbox().size() != 4) return null;

        double minLat = Double.parseDouble(dto.getBoundingbox().get(0));
        double maxLat = Double.parseDouble(dto.getBoundingbox().get(1));
        double minLon = Double.parseDouble(dto.getBoundingbox().get(2));
        double maxLon = Double.parseDouble(dto.getBoundingbox().get(3));

        return new GeoRegion(minLat, minLon, maxLat, maxLon, dto.getOsm_id());
    }
}
