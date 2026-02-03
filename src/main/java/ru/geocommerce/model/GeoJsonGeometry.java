package ru.geocommerce.model;

import java.util.List;

public class GeoJsonGeometry {
    public String type = "Polygon";
    public List<List<List<Double>>> coordinates;
}
