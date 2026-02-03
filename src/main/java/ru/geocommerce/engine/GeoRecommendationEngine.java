package ru.geocommerce.engine;

import org.springframework.stereotype.Component;
import ru.geocommerce.model.*;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GeoRecommendationEngine {

    private static final double EARTH_RADIUS_KM = 6371.0088;
    private static final double ALPHA = 1.0;
    private static final int BETA = 5;
    public static final double MIN_DISTANCE_TO_RETAIL = 1;
    public static final int MIN_PEOPLE_TO_RECOMMENDED = 300;

    public List<GeoRentPoint> filterRentPoints(List<GeoRentPoint> rentPoints, List<GeoRetailPoint> retailPoints) {
        return rentPoints.stream()
                .filter(rp -> calculateDistanceToNearestRetail(rp.getLat(), rp.getLon(), retailPoints) > MIN_DISTANCE_TO_RETAIL)
                .collect(Collectors.toList());
    }

    public List<GeoRentPoint> rankByTraffic(List<GeoRentPoint> rentPoints, List<Region> regions) {
        return rentPoints.stream()
                .filter(rp -> findPopulationNearPoint(rp.getLat(), rp.getLon(), regions) > MIN_PEOPLE_TO_RECOMMENDED)
                .collect(Collectors.toList());
    }

    public GeoJsonFeatureCollection getZones(List<Region> regions) {
        for(Region region : regions){
            region.setPotential(ALPHA * region.getPopulation() * Math.max(0, ((double) (BETA - region.getCountRetailPoints())) / ((double) BETA)));
        }

        quantizePotential(regions);

        return buildGeoJson(regions);
    }

    private void quantizePotential(List<Region> regions) {
        double min = regions.stream().mapToDouble(Region::getPotential).min().orElse(0);
        double max = regions.stream().mapToDouble(Region::getPotential).max().orElse(1);

        for (Region r : regions) {
            double norm = (r.getPotential() - min) / (max - min + 1e-9);

            if (norm < 0.35) r.setZoneId(0);
            else if (norm < 0.5) r.setZoneId(1);
            else if (norm < 0.75) r.setZoneId(2);
            else r.setZoneId(3);
        }
    }

    private boolean isNeighbor(Region a, Region b) {
        boolean latOverlap = a.getLatMin() <= b.getLatMax() && a.getLatMax() >= b.getLatMin();
        boolean lonOverlap = a.getLonMin() <= b.getLonMax() && a.getLonMax() >= b.getLonMin();

        boolean shareEdge =
                Math.abs(a.getLatMax() - b.getLatMin()) < 1e-9 ||
                        Math.abs(a.getLatMin() - b.getLatMax()) < 1e-9 ||
                        Math.abs(a.getLonMax() - b.getLonMin()) < 1e-9 ||
                        Math.abs(a.getLonMin() - b.getLonMax()) < 1e-9;

        return latOverlap && lonOverlap && shareEdge;
    }

    private Map<Integer, List<Region>> buildZones(List<Region> regions) {
        Map<Integer, List<Region>> zones = new HashMap<>();
        Set<Region> visited = new HashSet<>();
        int zoneCounter = 0;

        for (Region r : regions) {
            if (visited.contains(r)) continue;

            int level = r.getZoneId();
            List<Region> zone = new ArrayList<>();
            Queue<Region> q = new ArrayDeque<>();

            q.add(r);
            visited.add(r);

            while (!q.isEmpty()) {
                Region cur = q.poll();
                zone.add(cur);

                for (Region other : regions) {
                    if (!visited.contains(other)
                            && other.getZoneId() == level
                            && isNeighbor(cur, other)) {
                        visited.add(other);
                        q.add(other);
                    }
                }
            }

            zones.put(zoneCounter++, zone);
        }

        return zones;
    }

    private List<List<Double>> buildPolygon(List<Region> zone) {
        List<double[]> points = new ArrayList<>();

        for (Region r : zone) {
            points.add(new double[]{r.getLonMin(), r.getLatMin()});
            points.add(new double[]{r.getLonMax(), r.getLatMin()});
            points.add(new double[]{r.getLonMax(), r.getLatMax()});
            points.add(new double[]{r.getLonMin(), r.getLatMax()});
        }

        List<double[]> hull = convexHull(points);

        List<List<Double>> polygon = new ArrayList<>();
        for (double[] p : hull) {
            polygon.add(List.of(p[0], p[1]));
        }

        // замыкаем полигон
        polygon.add(polygon.get(0));

        return polygon;
    }

    private List<double[]> convexHull(List<double[]> pts) {
        pts.sort(Comparator.comparingDouble(a -> a[0]));

        List<double[]> lower = new ArrayList<>();
        for (double[] p : pts) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0)
                lower.remove(lower.size() - 1);
            lower.add(p);
        }

        List<double[]> upper = new ArrayList<>();
        for (int i = pts.size() - 1; i >= 0; i--) {
            double[] p = pts.get(i);
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0)
                upper.remove(upper.size() - 1);
            upper.add(p);
        }

        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);

        return lower;
    }

    private double cross(double[] a, double[] b, double[] c) {
        return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]);
    }

    private GeoJsonFeatureCollection buildGeoJson(List<Region> regions) {
        quantizePotential(regions);
        Map<Integer, List<Region>> zones = buildZones(regions);

        GeoJsonFeatureCollection collection = new GeoJsonFeatureCollection();

        String[] colors = {"#0000ff", "#00ff00", "#ffff00", "#ff0000"};

        for (List<Region> zone : zones.values()) {
            GeoJsonFeature feature = new GeoJsonFeature();

            int level = zone.get(0).getZoneId();
            feature.properties = Map.of(
                    "level", level,
                    "color", colors[level]
            );

            GeoJsonGeometry geometry = new GeoJsonGeometry();
//            geometry.coordinates = List.of(buildPolygon(zone));

            List<List<Double>> polygon = buildPolygon(zone);

            // Сглаживание
//            polygon = smoothPolygon(polygon, 3);

            geometry.coordinates = List.of(polygon);

            feature.geometry = geometry;
            collection.features.add(feature);
        }

        return collection;
    }

    private List<List<Double>> smoothPolygon(List<List<Double>> polygon, int iterations) {
        List<List<Double>> result = polygon;

        for (int it = 0; it < iterations; it++) {
            List<List<Double>> newPoints = new ArrayList<>();

            for (int i = 0; i < result.size() - 1; i++) {
                List<Double> p0 = result.get(i);
                List<Double> p1 = result.get(i + 1);

                double x0 = p0.get(0);
                double y0 = p0.get(1);
                double x1 = p1.get(0);
                double y1 = p1.get(1);

                // Q point
                newPoints.add(List.of(
                        0.75 * x0 + 0.25 * x1,
                        0.75 * y0 + 0.25 * y1
                ));

                // R point
                newPoints.add(List.of(
                        0.25 * x0 + 0.75 * x1,
                        0.25 * y0 + 0.75 * y1
                ));
            }

            // замыкаем полигон
            newPoints.add(newPoints.get(0));
            result = newPoints;
        }

        return result;
    }

    private static double calculateDistance(GeoCoordinates p1, GeoCoordinates p2) {
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

    private int findPopulationNearPoint(double lat, double lon, List<Region> regions) {
        for (Region region : regions) {
            if (lat > region.getLatMin() && lat < region.getLatMax() && lon > region.getLonMin() && lon < region.getLonMax()) {
                return region.getPopulation();
            }
        }
        return 0;
    }

    private double calculateDistanceToNearestRetail(double lat, double lon, List<GeoRetailPoint> retailPoints) {
        double dist = Double.MAX_VALUE;
        GeoCoordinates coordinatesRentPoint = new GeoCoordinates(lat, lon);
        for (GeoRetailPoint retailPoint : retailPoints) {
            GeoCoordinates coordinatesRetailPoint = new GeoCoordinates(retailPoint.getLat(), retailPoint.getLon());
            dist = Math.min(dist, calculateDistance(coordinatesRentPoint, coordinatesRetailPoint));
        }
        return dist;
    }
}