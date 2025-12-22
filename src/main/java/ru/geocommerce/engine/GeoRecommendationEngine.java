package ru.geocommerce.engine;

import org.springframework.stereotype.Component;
import ru.geocommerce.model.GeoCoordinates;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.model.GeoRetailPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GeoRecommendationEngine {

    public static final double MIN_DISTANCE_TO_RETAIL = 0.5; // км
    public static final int TOP_N_RECOMMENDED = 5;

    public List<GeoRentPoint> filterRentPoints(List<GeoRentPoint> rentPoints, int retailCount) {
        // Простая фильтрация: если рядом с точкой проката есть розничные точки — оставляем
        return rentPoints.stream()
                .filter(rp -> calculateDistanceToNearestRetail(rp, retailCount) < MIN_DISTANCE_TO_RETAIL)
                .collect(Collectors.toList());
    }

    public List<GeoRentPoint> rankByTraffic(List<GeoRentPoint> rentPoints, int population) {
        // Упрощённая логика: чем выше население — тем выше рейтинг
        return rentPoints.stream()
                .sorted((a, b) -> Integer.compare(population, 0)) // упрощённо
                .limit(TOP_N_RECOMMENDED)
                .collect(Collectors.toList());
    }

    public double calculateDistance(GeoCoordinates p1, GeoCoordinates p2) {
        // Упрощённое расстояние (на самом деле нужно использовать Haversine)
        double dLat = Math.toRadians(p2.getLat() - p1.getLat());
        double dLon = Math.toRadians(p2.getLon() - p1.getLon());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(p1.getLat())) * Math.cos(Math.toRadians(p2.getLat())) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6371 * c; // радиус Земли в км
    }

    private double calculateDistanceToNearestRetail(GeoRentPoint rp, int retailCount) {
        // Заглушка — в реальном коде тут был бы вызов к БД или сервису
        return Math.random() * 2; // от 0 до 2 км
    }
}