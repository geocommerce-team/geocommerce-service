package ru.geocommerce.model;

import java.io.Serializable;
import java.util.Objects;

public class GeoTrafficId implements Serializable {
    private int lat;
    private int lon;

    public GeoTrafficId() {}

    public GeoTrafficId(int lat, int lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GeoTrafficId)) return false;
        GeoTrafficId castOther = (GeoTrafficId) other;
        return Objects.equals(lat, castOther.lat) && Objects.equals(lon, castOther.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
