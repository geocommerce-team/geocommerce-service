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
import ru.geocommerce.cache.GeoCacheManager;

import java.util.List;

@RestController
@RequestMapping("/api/geocommerce")
public class GeoCommerceController {

    private final GeoRentPointsService rentPointsService;
    private final GeoRetailPointsService retailPointsService;
    private final GeoTrafficService trafficService;
    private final GeoRecommendationEngine recommendationEngine;
    private final GeoCacheManager cacheManager;

    public GeoCommerceController(GeoRentPointsService rentPointsService,
                                 GeoRetailPointsService retailPointsService,
                                 GeoTrafficService trafficService,
                                 GeoRecommendationEngine recommendationEngine,
                                 GeoCacheManager cacheManager) {
        this.rentPointsService = rentPointsService;
        this.retailPointsService = retailPointsService;
        this.trafficService = trafficService;
        this.recommendationEngine = recommendationEngine;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/recommendations")
    public ResponseEntity<GeoRecommendationResponse> getRecommendations(
            @RequestParam String category,
            @RequestParam double latMin,
            @RequestParam double lonMin,
            @RequestParam double latMax,
            @RequestParam double lonMax) {

        String bboxKey = String.format("%f_%f_%f_%f", latMin, lonMin, latMax, lonMax);

        // Проверяем кэш
        GeoRecommendationResponse cached = cacheManager.getRecommendations(bboxKey, category);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }
        // Получаем данные
        List<GeoRentPoint> rentPoints = rentPointsService.getRentPoints(lonMin, lonMax, latMax, latMin);
        List<GeoRetailPoint> retailPoints = retailPointsService.getRetailPoints(
                String.valueOf(category.hashCode()), latMin, lonMin, latMax, lonMax);
        int population = trafficService.getPopulation(latMin, lonMin, latMax, lonMax);

        // Фильтруем и ранжируем
        List<GeoRentPoint> filtered = recommendationEngine.filterRentPoints(rentPoints, retailPoints.size());
        List<GeoRentPoint> ranked = recommendationEngine.rankByTraffic(filtered, population);

        // Формируем ответ
        GeoRecommendationResponse response = new GeoRecommendationResponse(
                ranked, // recommended
                rentPoints, // regular
                retailPoints // retail
        );

        // Сохраняем в кэш
        cacheManager.saveRecommendations(bboxKey, category, response);

        return ResponseEntity.ok(response);
    }
}