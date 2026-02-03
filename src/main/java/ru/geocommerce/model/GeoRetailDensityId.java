package ru.geocommerce.model;

import java.io.Serializable;
import java.util.Objects;

public class GeoRetailDensityId implements Serializable {
    private String category;
    private int lat;
    private int lon;

    public GeoRetailDensityId() {}

    public GeoRetailDensityId(String category, int lat, int lon) {
        this.category = category;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GeoRetailDensityId)) return false;
        GeoRetailDensityId castOther = (GeoRetailDensityId) other;
        return Objects.equals(lat, castOther.lat) && Objects.equals(lon, castOther.lon) && Objects.equals(category, castOther.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, lat, lon);
    }
}
