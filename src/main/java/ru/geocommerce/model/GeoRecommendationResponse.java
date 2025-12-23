package ru.geocommerce.model;

import java.util.List;

public class GeoRecommendationResponse {
    private List<GeoRentPoint> recommendedRentPoints;
    private List<GeoRentPoint> regularRentPoints;
    private List<GeoRetailPoint> retailPoints;

    public GeoRecommendationResponse() {}

    public GeoRecommendationResponse(List<GeoRentPoint> recommendedRentPoints,
                                     List<GeoRentPoint> regularRentPoints,
                                     List<GeoRetailPoint> retailPoints) {
        this.recommendedRentPoints = recommendedRentPoints;
        this.regularRentPoints = regularRentPoints;
        this.retailPoints = retailPoints;
    }

    public List<GeoRentPoint> getRecommendedRentPoints() { return recommendedRentPoints; }
    public void setRecommendedRentPoints(List<GeoRentPoint> recommendedRentPoints) { this.recommendedRentPoints = recommendedRentPoints; }
    public List<GeoRentPoint> getRegularRentPoints() { return regularRentPoints; }
    public void setRegularRentPoints(List<GeoRentPoint> regularRentPoints) { this.regularRentPoints = regularRentPoints; }
    public List<GeoRetailPoint> getRetailPoints() { return retailPoints; }
    public void setRetailPoints(List<GeoRetailPoint> retailPoints) { this.retailPoints = retailPoints; }
}