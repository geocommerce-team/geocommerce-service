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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            @RequestParam double lonMax) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<List<GeoRentPoint>> rentFuture = executor.submit(() ->
                getRentPoints(latMin, latMax, lonMin, lonMax)
        );

        Future<List<GeoRetailPoint>> retailFuture = executor.submit(() ->
                getRetailPoints(latMin, latMax, lonMin, lonMax, category)
        );

        Future<List<Region>> regionsFuture = executor.submit(() ->
                getRegions(latMin, latMax, lonMin, lonMax)
        );

        List<GeoRentPoint> rentPoints = rentFuture.get();
        List<GeoRetailPoint> retailPoints = retailFuture.get();
        List<Region> regions = regionsFuture.get();

        executor.shutdown();


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

    private List<GeoRentPoint> getRentPoints(double latMin, double latMax, double lonMin, double lonMax) {
        double deltaLat = latMax / Math.abs(latMax);
        double deltaLon = lonMax / Math.abs(lonMax);
        double delta = 100.0;

        latMin = Math.round(latMin * delta);
        latMax = Math.round(latMax * delta + deltaLat);
        lonMin = Math.round(lonMin * delta);
        lonMax = Math.round(lonMax * delta + deltaLon);

        List<Double> latitudes = new ArrayList<>();
        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);

        List<Double> longitudes = new ArrayList<>();
        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);

        // Параллельный цикл через stream
        return latitudes.parallelStream()
                .flatMap(lat ->
                        longitudes.parallelStream()
                                .flatMap(lon ->
                                        rentPointsService.getRentPoints(
                                                lon / delta,
                                                (lon + deltaLon) / delta,
                                                (lat + deltaLat) / delta,
                                                lat / delta
                                        ).stream()
                                )
                )
                .toList();
    }


    private List<GeoRetailPoint> getRetailPoints(double latMin, double latMax, double lonMin, double lonMax, String category) {
        double deltaLat = latMax / Math.abs(latMax);
        double deltaLon = lonMax / Math.abs(lonMax);
        double delta = 100.0;

        latMin = Math.round(latMin * delta);
        latMax = Math.round(latMax * delta + deltaLat);
        lonMin = Math.round(lonMin * delta);
        lonMax = Math.round(lonMax * delta + deltaLon);

        List<Double> latitudes = new ArrayList<>();
        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);

        List<Double> longitudes = new ArrayList<>();
        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);

        return latitudes.parallelStream()
                .flatMap(lat ->
                        longitudes.parallelStream()
                                .flatMap(lon ->
                                        retailPointsService.getRetailPoints(
                                                category,
                                                lat / delta,
                                                lon / delta,
                                                (lat + deltaLat) / delta,
                                                (lon + deltaLon) / delta
                                        ).stream()
                                )
                )
                .toList();
    }


    private List<Region> getRegions(double latMin, double latMax, double lonMin, double lonMax) {
        double deltaLat = latMax / Math.abs(latMax);
        double deltaLon = lonMax / Math.abs(lonMax);
        double delta = 10000.0;

        latMin = Math.round(latMin * delta);
        latMax = Math.round(latMax * delta + deltaLat);
        lonMin = Math.round(lonMin * delta);
        lonMax = Math.round(lonMax * delta + deltaLon);

        List<Double> latitudes = new ArrayList<>();
        for (double lat = latMin; lat < latMax; lat += deltaLat) latitudes.add(lat);

        List<Double> longitudes = new ArrayList<>();
        for (double lon = lonMin; lon < lonMax; lon += deltaLon) longitudes.add(lon);

        return latitudes.parallelStream()
                .flatMap(lat ->
                        longitudes.parallelStream()
                                .map(lon -> new Region(
                                        lat / delta,
                                        (lat + deltaLat) / delta,
                                        lon / delta,
                                        (lon + deltaLon) / delta,
                                        trafficService.getPopulation(
                                                lat / delta,
                                                lon / delta,
                                                (lat + deltaLat) / delta,
                                                (lon + deltaLon) / delta
                                        )
                                ))
                )
                .toList();
    }

}