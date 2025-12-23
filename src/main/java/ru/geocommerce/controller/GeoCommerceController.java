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
    private final double delta = 100.0;
    
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

        latMin = Math.round(latMin * delta);
        latMax = Math.round(latMax * delta + deltaLat);
        lonMin = Math.round(lonMin * delta);
        lonMax = Math.round(lonMax * delta + deltaLon);

        ArrayList<GeoRentPoint> rentPoints = new ArrayList<>();
        ArrayList<GeoRetailPoint> retailPoints = new ArrayList<>();
        ArrayList<Region> regions = new ArrayList<>();
        ArrayList<GeoRecommendationResponse> responses = new ArrayList<>();

        for (double lat = latMin; lat < latMax; lat += deltaLat) {
            for (double lon = lonMin; lon < lonMax; lon += deltaLon) {

                rentPoints.addAll(rentPointsService.getRentPoints(
                        lon / delta,
                        (lon + deltaLon) / delta,
                        (lat + deltaLat) / delta,
                        lat / delta));
                retailPoints.addAll(retailPointsService.getRetailPoints(
                        category,
                        lat / delta,
                        lon / delta,
                        (lat + deltaLat) / delta,
                        (lon + deltaLon) / delta));
                Region region = new Region(lat / delta,
                        (lat + deltaLat) / delta,
                        lon / delta,
                        (lon + deltaLon) / delta,
                        trafficService.getPopulation(
                                lat / delta,
                                lon / delta,
                                (lat + deltaLat) / delta,
                                (lon + deltaLon) / delta
                        ));
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