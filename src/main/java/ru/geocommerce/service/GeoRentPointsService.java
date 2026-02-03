package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.extern.client.RentPointsClient;
import ru.geocommerce.extern.dto.RentPointDto;
import ru.geocommerce.model.GeoRegion;
import ru.geocommerce.model.GeoRelevance;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.repository.GeoRelevanceRepository;
import ru.geocommerce.repository.GeoRentPointRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.geocommerce.controller.GeoCommerceController.DELTA;

@Service
public class GeoRentPointsService {

    private final RentPointsClient rentPointsClient;
    private final GeoRentPointRepository geoRentPointRepository;
    private final GeoRelevanceRepository geoRelevanceRepository;

    private static final long CACHE_MONTH = 6;

    public GeoRentPointsService(RentPointsClient rentPointsClient,
                                GeoRentPointRepository geoRentPointRepository, GeoRelevanceRepository geoRelevanceRepository) {
        this.rentPointsClient = rentPointsClient;
        this.geoRentPointRepository = geoRentPointRepository;
        this.geoRelevanceRepository = geoRelevanceRepository;
    }

    @Transactional
    public void updateOrLoadRegion(GeoRegion geoRegion) {
        List<GeoRelevance> info = geoRelevanceRepository.findInfo(geoRegion.osm_id, "rent");
        if(!info.isEmpty() && LocalDate.now().minusMonths(CACHE_MONTH).isBefore(info.getFirst().getUpdated())) {
            return;
        }

        double latMin = geoRegion.minLat;
        double latMax = geoRegion.maxLat;
        double lonMin = geoRegion.minLon;
        double lonMax = geoRegion.maxLon;

        if(!info.isEmpty()) {
            int _latMin = info.getFirst().getLat_min();
            int _lonMin = info.getFirst().getLon_min();
            int _latMax = info.getFirst().getLat_max();
            int _lonMax = info.getFirst().getLon_max();
            geoRentPointRepository.deleteFreshByBounds(((double) _latMin) / DELTA, ((double) _latMax) / DELTA, ((double) _lonMin) / DELTA, ((double) _lonMax) / DELTA);
        }
        else{
            //TODO: INFO пришел пустой
            double deltaLat = latMax / Math.abs(latMax);
            double deltaLon = lonMax / Math.abs(lonMax);
            GeoRelevance geoRelevance = new GeoRelevance(
                    (int) (latMin * DELTA),
                    (int) (latMax * DELTA + deltaLat),
                    (int) (lonMin * DELTA),
                    (int) (lonMax * DELTA + deltaLon),
                    geoRegion.osm_id,
                    "rent"
            );
            geoRelevanceRepository.save(geoRelevance);
        }

//        double deltaLat = latMax / Math.abs(latMax);
//        double deltaLon = lonMax / Math.abs(lonMax);
//        latMin = Math.floor(latMin * DELTA);
//        latMax = Math.floor(latMax * DELTA + deltaLat);
//        lonMin = Math.floor(lonMin * DELTA);
//        lonMax = Math.floor(lonMax * DELTA + deltaLon);
//
//        List<Double> latitudes = new ArrayList<>();
//        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);
//
//        List<Double> longitudes = new ArrayList<>();
//        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);
//
//        List<List<RentPointDto>> dtos = latitudes.parallelStream()
//                .flatMap(lat ->
//                        longitudes.parallelStream()
//                                .map(lon ->
//                                        rentPointsClient.getRentPoints(
//                                                lat / DELTA,
//                                                lon / DELTA,
//                                                (lat + deltaLat) / DELTA,
//                                                (lon + deltaLon) / DELTA
//                                        )
//                                )
//                )
//                .toList();

//        List<RentPointDto> unionDtos = new ArrayList<>();
//        for(List<RentPointDto> dto : dtos) {
//            unionDtos.addAll(dto);
//        }

        List<RentPointDto> unionDtos = rentPointsClient.getRentPoints(latMin, lonMin, latMax, lonMax);
        List<GeoRentPoint> freshPoints = unionDtos.stream()
                .map(dto -> new GeoRentPoint(
                        dto.getLatitude(),
                        dto.getLongitude(),
                        "",
                        "rent"
                ))
                .collect(Collectors.toList());

        geoRentPointRepository.saveAll(freshPoints);

        geoRelevanceRepository.updateInfo(LocalDate.now(), geoRegion.osm_id, "rent");
    }

    @Transactional
    public List<GeoRentPoint> getRentPoints(double latMin, double lonMin, double latMax, double lonMax, long placeId) {
        List<GeoRelevance> info = geoRelevanceRepository.findInfo(placeId, "rent");
        if(!info.isEmpty() && LocalDate.now().minusMonths(CACHE_MONTH).isBefore(info.getFirst().getUpdated())) {
            List<GeoRentPoint> cached = geoRentPointRepository.findFreshByBounds(latMin, lonMin, latMax, lonMax);
            if (!cached.isEmpty()) {
                return cached;
            }
        }

        return new ArrayList<>();
    }
}