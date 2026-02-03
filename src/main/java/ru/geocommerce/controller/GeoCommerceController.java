package ru.geocommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geocommerce.engine.GeoRecommendationEngine;
import ru.geocommerce.model.*;
import ru.geocommerce.service.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class GeoCommerceController {

    public static final double DELTA = 100.0;

    private final GeoRentPointsService rentPointsService;
    private final GeoRetailPointsService retailPointsService;
    private final GeoTrafficService trafficService;
    private final NominatimService nominatimService;
    private final GeoRetailPointsDensityService retailPointsDensityService;
    private final GeoRecommendationEngine recommendationEngine;

    public GeoCommerceController(GeoRentPointsService rentPointsService,
                                 GeoRetailPointsService retailPointsService,
                                 GeoTrafficService trafficService,
                                 NominatimService nominatimService,
                                 GeoRetailPointsDensityService retailPointsDensityService,
                                 GeoRecommendationEngine recommendationEngine) {
        this.rentPointsService = rentPointsService;
        this.retailPointsService = retailPointsService;
        this.trafficService = trafficService;
        this.nominatimService = nominatimService;
        this.retailPointsDensityService = retailPointsDensityService;
        this.recommendationEngine = recommendationEngine;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<GeoRecommendationResponse> getRecommendations(
            @RequestParam String category,
            @RequestParam double latMin,
            @RequestParam double lonMin,
            @RequestParam double latMax,
            @RequestParam double lonMax) {

        List<GeoRegion> geoRegions = nominatimService.getCity(latMin, lonMin, latMax, lonMax);

        GeoRecommendationResponse response = new GeoRecommendationResponse();

        for (GeoRegion region : geoRegions) {
            if(region.osm_id != 366544){
                continue;
            }
            response.combine(getResponse(category, latMin, lonMin, latMax, lonMax, region));
        }

        return ResponseEntity.ok(response);
    }

    private GeoRecommendationResponse getResponse(String category, double latMin, double lonMin, double latMax, double lonMax, GeoRegion geoRegion) {
        final double finalLatMin = geoRegion.getMinLat();
        final double finalLonMin = geoRegion.getMinLon();
        final double finalLatMax = geoRegion.getMaxLat();
        final double finalLonMax = geoRegion.getMaxLon();

        rentPointsService.updateOrLoadRegion(geoRegion);
        retailPointsService.updateOrLoadRegion(category, geoRegion);
        trafficService.updateOrLoadRegion(geoRegion);
        retailPointsDensityService.updateOrLoadRegion(retailPointsService, category, geoRegion);

        List<GeoRentPoint> rentPoints = getRentPoints(latMin, latMax, lonMin, lonMax, geoRegion.osm_id);
        List<GeoRetailPoint> retailPoints = getRetailPoints(latMin, latMax, lonMin, lonMax, category, geoRegion.osm_id);
        List<Region> regions = getRegions(finalLatMin, finalLatMax, finalLonMin, finalLonMax, category, geoRegion.osm_id);

        List<GeoRentPoint> filtered = recommendationEngine.filterRentPoints(rentPoints, retailPoints);
        List<GeoRentPoint> ranked = recommendationEngine.rankByTraffic(filtered, regions);

        ArrayList<GeoRentPoint> otherRentPoints = new ArrayList<>();

        for (GeoRentPoint point : rentPoints) {
            if (ranked.contains(point)) {
                point.setType("recommendation");
            } else {
                otherRentPoints.add(point);
            }
        }

        GeoJsonFeatureCollection featureCollection = recommendationEngine.getZones(regions);

        return new GeoRecommendationResponse(
                ranked,
                otherRentPoints,
                retailPoints,
                featureCollection
        );
    }

    private List<GeoRentPoint> getRentPoints(double latMin, double latMax, double lonMin, double lonMax, long placeId) {
        return rentPointsService.getRentPoints(
                latMin,
                lonMin,
                latMax,
                lonMax,
                placeId
        );
    }


    private List<GeoRetailPoint> getRetailPoints(double latMin, double latMax, double lonMin, double lonMax, String category, long placeId) {
        return retailPointsService.getRetailPoints(
                category,
                latMin,
                lonMin,
                latMax,
                lonMax,
                placeId
        );
    }

    private List<Region> getRegions(double latMin, double latMax, double lonMin, double lonMax, String category, long placeId) {
        double deltaLat = latMax / Math.abs(latMax);
        double deltaLon = lonMax / Math.abs(lonMax);
        double DELTA = 100.0;
        latMin = Math.floor(latMin * DELTA);
        latMax = Math.floor(latMax * DELTA + deltaLat);
        lonMin = Math.floor(lonMin * DELTA);
        lonMax = Math.floor(lonMax * DELTA + deltaLon);

        List<Double> latitudes = new ArrayList<>();
        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);

        List<Double> longitudes = new ArrayList<>();
        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);

        return latitudes.parallelStream()
                .flatMap(lat ->
                        longitudes.parallelStream()
                                .map(lon -> new Region(
                                        lat / DELTA,
                                        (lat + deltaLat) / DELTA,
                                        lon / DELTA,
                                        (lon + deltaLon) / DELTA,
                                        trafficService.getPopulation(
                                                lat / DELTA,
                                                lon / DELTA,
                                                placeId
                                        ),
                                        retailPointsDensityService.getRetailPoints(
                                                category,
                                                lat / DELTA,
                                                lon / DELTA,
                                                placeId
                                        ),
                                        1.0,
                                        -1
                                ))
                )
                .toList();
    }
}