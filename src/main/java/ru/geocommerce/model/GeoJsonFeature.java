package ru.geocommerce.model;

import java.util.Map;

public class GeoJsonFeature {
    public String type = "Feature";
    public Map<String, Object> properties;
    public GeoJsonGeometry geometry;
}