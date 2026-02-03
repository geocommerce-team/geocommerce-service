package ru.geocommerce.model;

import java.util.ArrayList;
import java.util.List;

public class GeoJsonFeatureCollection {
    public String type = "FeatureCollection";
    public List<GeoJsonFeature> features = new ArrayList<>();
}