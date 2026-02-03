package ru.geocommerce.model;

import java.io.Serializable;
import java.util.Objects;

public class GeoRetailPointId implements Serializable {
    private double lat;
    private double lon;
    private String name;
    private String category;

    public GeoRetailPointId() {}

    public GeoRetailPointId(double lat, double lon, String name, String category) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.category = category;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GeoRetailPointId)) return false;
        GeoRetailPointId castOther = (GeoRetailPointId) other;
        return Objects.equals(lat, castOther.lat) && Objects.equals(lon, castOther.lon) && Objects.equals(name, castOther.name) &&Objects.equals(category, castOther.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon, name, category);
    }
}
