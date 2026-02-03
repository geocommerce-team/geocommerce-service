package ru.geocommerce.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GeoRecommendationResponse {
    @Setter
    @Getter
    private List<GeoRentPoint> recommendedRentPoints;
    @Setter
    @Getter
    private List<GeoRentPoint> regularRentPoints;
    @Setter
    @Getter
    private List<GeoRetailPoint> retailPoints;
    @Setter
    @Getter
    private GeoJsonFeatureCollection zones;

    public GeoRecommendationResponse() {
        recommendedRentPoints = new ArrayList<GeoRentPoint>();
        retailPoints = new ArrayList<>();
        regularRentPoints = new ArrayList<>();
        zones = new GeoJsonFeatureCollection();
    }

    public GeoRecommendationResponse(List<GeoRentPoint> recommendedRentPoints,
                                     List<GeoRentPoint> regularRentPoints,
                                     List<GeoRetailPoint> retailPoints) {
        this.recommendedRentPoints = recommendedRentPoints;
        this.regularRentPoints = regularRentPoints;
        this.retailPoints = retailPoints;
        this.zones = new GeoJsonFeatureCollection();
    }

    public GeoRecommendationResponse(List<GeoRentPoint> recommendedRentPoints,
                                     List<GeoRentPoint> regularRentPoints,
                                     List<GeoRetailPoint> retailPoints,
                                     GeoJsonFeatureCollection zones) {
        this.recommendedRentPoints = recommendedRentPoints;
        this.regularRentPoints = regularRentPoints;
        this.retailPoints = retailPoints;
        this.zones = zones;
    }

    public void combine(GeoRecommendationResponse other) {
        recommendedRentPoints.addAll(other.recommendedRentPoints);
        regularRentPoints.addAll(other.regularRentPoints);
        retailPoints.addAll(other.retailPoints);
        zones.features.addAll(other.zones.features);
    }
}