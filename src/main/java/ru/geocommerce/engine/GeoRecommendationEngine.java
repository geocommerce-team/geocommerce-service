package ru.geocommerce.engine;

import org.springframework.stereotype.Component;
import ru.geocommerce.controller.GeoCommerceController;
import ru.geocommerce.model.GeoCoordinates;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.model.GeoRetailPoint;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GeoRecommendationEngine {

    private static final double EARTH_RADIUS_KM = 6371.0088;
    public static final double MIN_DISTANCE_TO_RETAIL = 0.3;
    public static final int MIN_PEOPLE_TO_RECOMMENDED = 100;

    public List<GeoRentPoint> filterRentPoints(List<GeoRentPoint> rentPoints, List<GeoRetailPoint> retailPoints) {
        return rentPoints.stream()
                .filter(rp -> calculateDistanceToNearestRetail(rp, retailPoints) > MIN_DISTANCE_TO_RETAIL)
                .collect(Collectors.toList());
    }

    public List<GeoRentPoint> rankByTraffic(List<GeoRentPoint> rentPoints, List<GeoCommerceController.Region> regions) {
        return rentPoints.stream()
                .filter(rp -> findPopulationNearPoint(rp.getLat(), rp.getLon(), regions) > MIN_PEOPLE_TO_RECOMMENDED)
                .collect(Collectors.toList());
    }

    public static double calculateDistance(GeoCoordinates p1, GeoCoordinates p2) {
        double lat1 = Math.toRadians(p1.getLat());
        double lon1 = Math.toRadians(p1.getLon());
        double lat2 = Math.toRadians(p2.getLat());
        double lon2 = Math.toRadians(p2.getLon());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private int findPopulationNearPoint(double lat, double lon, List<GeoCommerceController.Region> regions) {
        for (GeoCommerceController.Region region : regions) {
            if(lat > region.latMin() && lat < region.latMax() && lon > region.lonMin() && lon < region.lonMax()) {
                return region.countPeoples();
            }
        }
        return 0;
    }

    private double calculateDistanceToNearestRetail(GeoRentPoint rentPoint, List<GeoRetailPoint> retailPoints) {
        double dist = Double.MAX_VALUE;
        GeoCoordinates coordinatesRentPoint = new GeoCoordinates(rentPoint.getLat(), rentPoint.getLon());
        for (GeoRetailPoint retailPoint : retailPoints) {
            GeoCoordinates coordinatesRetailPoint = new GeoCoordinates(retailPoint.getLat(), retailPoint.getLon());
            dist = Math.min(dist, calculateDistance(coordinatesRentPoint, coordinatesRetailPoint));
        }
        return dist;
    }
}