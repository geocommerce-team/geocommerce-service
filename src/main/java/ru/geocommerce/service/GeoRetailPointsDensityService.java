package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.model.*;
import ru.geocommerce.repository.GeoRelevanceRepository;
import ru.geocommerce.repository.GeoRetailPointsDensityRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.geocommerce.controller.GeoCommerceController.DELTA;

@Service
public class GeoRetailPointsDensityService {
    private final GeoRetailPointsDensityRepository geoRetailPointsDensityRepository;
    private final GeoRelevanceRepository geoRelevanceRepository;

    private static final long CACHE_MONTH = 6;
    private final GeoRetailPointsService geoRetailPointsService;

    public GeoRetailPointsDensityService(GeoRetailPointsDensityRepository geoRetailPointsDensityRepository, GeoRelevanceRepository geoRelevanceRepository, GeoRetailPointsService geoRetailPointsService) {
        this.geoRetailPointsDensityRepository = geoRetailPointsDensityRepository;
        this.geoRelevanceRepository = geoRelevanceRepository;
        this.geoRetailPointsService = geoRetailPointsService;
    }

    @Transactional
    public void updateOrLoadRegion(GeoRetailPointsService geoRetailPointsService, String category, GeoRegion geoRegion) {
        List<GeoRelevance> info = geoRelevanceRepository.findInfo(geoRegion.osm_id, category + "_density");
        if(!info.isEmpty() && LocalDate.now().minusMonths(CACHE_MONTH).isBefore(info.getFirst().getUpdated())) {
            return;
        }

        double latMin = geoRegion.minLat;
        double latMax = geoRegion.maxLat;
        double lonMin = geoRegion.minLon;
        double lonMax = geoRegion.maxLon;

        double deltaLat = latMax / Math.abs(latMax);
        double deltaLon = lonMax / Math.abs(lonMax);

        if(!info.isEmpty()) {
            int _latMin = info.getFirst().getLat_min();
            int _lonMin = info.getFirst().getLon_min();
            int _latMax = info.getFirst().getLat_max();
            int _lonMax = info.getFirst().getLon_max();
            geoRetailPointsDensityRepository.deleteFreshByBoundsAndCategory(_latMin, _latMax, _lonMin, _lonMax, category);
        }
        else{
            GeoRelevance geoRelevance = new GeoRelevance(
                    (int) (latMin * DELTA),
                    (int) (latMax * DELTA + deltaLat),
                    (int) (lonMin * DELTA),
                    (int) (lonMax * DELTA + deltaLon),
                    geoRegion.osm_id,
                    category + "_density"
            );
            geoRelevanceRepository.save(geoRelevance);
        }

        latMin = Math.floor(latMin * DELTA);
        latMax = Math.floor(latMax * DELTA + deltaLat);
        lonMin = Math.floor(lonMin * DELTA);
        lonMax = Math.floor(lonMax * DELTA + deltaLon);

        List<Double> latitudes = new ArrayList<>();
        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);

        List<Double> longitudes = new ArrayList<>();
        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);

        List<GeoRetailDensity> dtos = latitudes.parallelStream()
                .flatMap(lat ->
                        longitudes.parallelStream()
                                .map(lon -> new GeoRetailDensity(
                                        category,
                                        lat.intValue(),
                                        lon.intValue(),
                                        geoRetailPointsService.getRetailPoints(
                                                category,
                                                lat / DELTA,
                                                lon / DELTA,
                                                (lat + deltaLat) / DELTA,
                                                (lon + deltaLon) / DELTA,
                                                geoRegion.osm_id
                                        ).size()
                                    )
                                )
                )
                .toList();

        geoRetailPointsDensityRepository.saveAll(dtos);

        geoRelevanceRepository.updateInfo(LocalDate.now(), geoRegion.osm_id, category + "_density");
    }

    @Transactional
    public int getRetailPoints(String category,
                               double lat, double lon, long placeId) {
        List<GeoRelevance> info = geoRelevanceRepository.findInfo(placeId, category + "_density");
        if (!info.isEmpty() && LocalDate.now().minusMonths(CACHE_MONTH).isBefore(info.getFirst().getUpdated())) {
            List<GeoRetailDensity> cached = geoRetailPointsDensityRepository
                    .findFreshByBoundsAndCategory(
                            category,
                            (int) (lat * DELTA),
                            (int) (lon * DELTA)
                    );

            if (!cached.isEmpty()) {
                return cached.getFirst().getCount_points();
            }
        }
        return 0;
    }
}
