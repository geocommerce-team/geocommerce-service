package ru.geocommerce.cache;

import org.springframework.stereotype.Component;
import ru.geocommerce.model.GeoRecommendationResponse;

import java.util.HashMap;
import java.util.Map;

@Component
public class GeoCacheManager {

    private final Map<String, Object> cache = new HashMap<>();

    public <T> T getRentPoints(String bboxKey, String category) {
        String key = "rent_" + bboxKey + "_" + category;
        return (T) cache.get(key);
    }

    public void saveRentPoints(String bboxKey, String category, Object points) {
        String key = "rent_" + bboxKey + "_" + category;
        cache.put(key, points);
    }

    public <T> T getRetailPoints(String bboxKey, String category) {
        String key = "retail_" + bboxKey + "_" + category;
        return (T) cache.get(key);
    }

    public void saveRetailPoints(String bboxKey, String category, Object points) {
        String key = "retail_" + bboxKey + "_" + category;
        cache.put(key, points);
    }

    public Integer getPopulation(String bboxKey) {
        String key = "pop_" + bboxKey;
        return (Integer) cache.get(key);
    }

    public void savePopulation(String bboxKey, int population) {
        String key = "pop_" + bboxKey;
        cache.put(key, population);
    }

    public GeoRecommendationResponse getRecommendations(String bboxKey, String category) {
        String key = "rec_" + bboxKey + "_" + category;
        return (GeoRecommendationResponse) cache.get(key);
    }

    public void saveRecommendations(String bboxKey, String category, GeoRecommendationResponse response) {
        String key = "rec_" + bboxKey + "_" + category;
        cache.put(key, response);
    }
}