package ru.geocommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.geocommerce.engine.GeoRecommendationEngine;
import ru.geocommerce.model.GeoRecommendationResponse;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.model.GeoRetailPoint;
import ru.geocommerce.service.GeoRentPointsService;
import ru.geocommerce.service.GeoRetailPointsService;
import ru.geocommerce.service.GeoTrafficService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GeoCommerceController {

    private final GeoRentPointsService rentPointsService;
    private final GeoRetailPointsService retailPointsService;
    private final GeoTrafficService trafficService;
    private final GeoRecommendationEngine recommendationEngine;

    public record Region(double latMin, double latMax, double lonMin, double lonMax, int countPeoples) {}

    public GeoCommerceController(GeoRentPointsService rentPointsService,
                                 GeoRetailPointsService retailPointsService,
                                 GeoTrafficService trafficService,
                                 GeoRecommendationEngine recommendationEngine) {
        this.rentPointsService = rentPointsService;
        this.retailPointsService = retailPointsService;
        this.trafficService = trafficService;
        this.recommendationEngine = recommendationEngine;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<GeoRecommendationResponse> getRecommendations(
            @RequestParam String category,
            @RequestParam double latMin,
            @RequestParam double lonMin,
            @RequestParam double latMax,
            @RequestParam double lonMax) {


        double deltaLat = latMax / Math.abs(latMax);
        double deltaLon = lonMax / Math.abs(lonMax);

        latMin = Math.round(latMin * 10000.0);
        latMax = Math.round(latMax * 10000.0 + deltaLat);
        lonMin = Math.round(lonMin * 10000.0);
        lonMax = Math.round(lonMax * 10000.0 + deltaLon);

        ArrayList<GeoRentPoint> rentPoints = new ArrayList<>();
        ArrayList<GeoRetailPoint> retailPoints = new ArrayList<>();
        ArrayList<Region> regions = new ArrayList<>();
        ArrayList<GeoRecommendationResponse> responses = new ArrayList<>();

        for (double lat = latMin; lat < latMax; lat += deltaLat) {
            for (double lon = lonMin; lon < lonMax; lon += deltaLon) {

                rentPoints.addAll(rentPointsService.getRentPoints(lonMin, lonMax, latMax, latMin));
                retailPoints.addAll(retailPointsService.getRetailPoints(
                        category, latMin, lonMin, latMax, lonMax));
                Region region = new Region(lat / 10000.0,
                        lon / 10000.0,
                        (lat + deltaLat) / 10000.0,
                        (lon + deltaLon) / 10000.0,
                        trafficService.getPopulation(latMin, lonMin, latMax, lonMax));
                regions.add(region);
            }
        }

        List<GeoRentPoint> filtered = recommendationEngine.filterRentPoints(rentPoints, retailPoints);
        List<GeoRentPoint> ranked = recommendationEngine.rankByTraffic(filtered, regions);

        ArrayList<GeoRentPoint> otherRentPoints = new ArrayList<>();


        for(GeoRentPoint point : rentPoints) {
            if(ranked.contains(point)) {
                point.setType("recommendation");
            }
            else{
                otherRentPoints.add(point);
            }
        }

        GeoRecommendationResponse response = new GeoRecommendationResponse(
                ranked,
                otherRentPoints,
                retailPoints
        );

        return ResponseEntity.ok(response);
    }
}