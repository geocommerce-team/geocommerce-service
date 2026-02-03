package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.extern.client.RetailPointsClient;
import ru.geocommerce.extern.dto.RetailPointDto;
import ru.geocommerce.model.GeoRegion;
import ru.geocommerce.model.GeoRelevance;
import ru.geocommerce.model.GeoRetailPoint;
import ru.geocommerce.repository.GeoRelevanceRepository;
import ru.geocommerce.repository.GeoRetailPointRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.geocommerce.controller.GeoCommerceController.DELTA;

@Service
public class GeoRetailPointsService {

    private final RetailPointsClient retailPointsClient;
    private final GeoRetailPointRepository geoRetailPointRepository;
    private final GeoRelevanceRepository geoRelevanceRepository;

    private static final long CACHE_MONTH = 6;

    public GeoRetailPointsService(RetailPointsClient retailPointsClient,
                                  GeoRetailPointRepository geoRetailPointRepository, GeoRelevanceRepository geoRelevanceRepository) {
        this.retailPointsClient = retailPointsClient;
        this.geoRetailPointRepository = geoRetailPointRepository;
        this.geoRelevanceRepository = geoRelevanceRepository;
    }

    @Transactional
    public void updateOrLoadRegion(String category, GeoRegion geoRegion){
        List<GeoRelevance> info = geoRelevanceRepository.findInfo(geoRegion.osm_id, category);
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
            geoRetailPointRepository.deleteFreshByBoundsAndCategory(((double) _latMin) / DELTA, ((double) _latMax) / DELTA, ((double) _lonMin) / DELTA, ((double) _lonMax) / DELTA, category);
        }
        else{
            GeoRelevance geoRelevance = new GeoRelevance(
                    (int) (latMin * DELTA),
                    (int) (latMax * DELTA + deltaLat),
                    (int) (lonMin * DELTA),
                    (int) (lonMax * DELTA + deltaLon),
                    geoRegion.osm_id,
                    category
            );
            geoRelevanceRepository.save(geoRelevance);
        }
//        latMin = Math.floor(latMin * DELTA);
//        latMax = Math.floor(latMax * DELTA + deltaLat);
//        lonMin = Math.floor(lonMin * DELTA);
//        lonMax = Math.floor(lonMax * DELTA + deltaLon);

//        List<Double> latitudes = new ArrayList<>();
//        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);
//
//        List<Double> longitudes = new ArrayList<>();
//        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);

//        List<List<RetailPointDto>> dtos = latitudes.parallelStream()
//                .flatMap(lat ->
//                        longitudes.parallelStream()
//                                .map(lon ->
//                                        retailPointsClient.getRetailPoints(
//                                                category,
//                                                lat / DELTA,
//                                                lon / DELTA,
//                                                (lat + deltaLat) / DELTA,
//                                                (lon + deltaLon) / DELTA
//                                        )
//                                )
//                )
//                .toList();
//
//        List<RetailPointDto> unionDtos = new ArrayList<>();
//        for(List<RetailPointDto> dto : dtos) {
//            unionDtos.addAll(dto);
//        }

        List<RetailPointDto> unionDtos = retailPointsClient.getRetailPoints(
                category,
                latMin,
                lonMin,
                latMax,
                lonMax
                );

        List<GeoRetailPoint> freshPoints = unionDtos.stream()
                .filter(dto -> dto.lat() != null && dto.lon() != null)
                .map(dto -> new GeoRetailPoint(
                        dto.lat(),
                        dto.lon(),
                        dto.name(),
                        category
                ))
                .collect(Collectors.toList());

        geoRetailPointRepository.saveAll(freshPoints);

        geoRelevanceRepository.updateInfo(LocalDate.now(), geoRegion.osm_id, category);
    }

    @Transactional
    public List<GeoRetailPoint> getRetailPoints(String category,
                                                double latMin, double lonMin,
                                                double latMax, double lonMax, long placeId) {

        List<GeoRelevance> info = geoRelevanceRepository.findInfo(placeId, category);
        if (!info.isEmpty() && LocalDate.now().minusMonths(CACHE_MONTH).isBefore(info.getFirst().getUpdated())) {
            List<GeoRetailPoint> cached = geoRetailPointRepository
                    .findFreshByBoundsAndCategory(category, latMin, latMax, lonMin, lonMax);
            if (!cached.isEmpty()) {
                return cached;
            }
        }
        return new ArrayList<>();
    }
}