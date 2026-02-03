package ru.geocommerce.model;

import jakarta.persistence.Id;

import java.util.Objects;

public class GeoRentPointId implements java.io.Serializable {
    private double lat;
    private double lon;
    private String name;
    private String type;

    public GeoRentPointId() {}

    public GeoRentPointId(double lat, double lon, String name, String type) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GeoRentPointId)) return false;
        GeoRentPointId castOther = (GeoRentPointId) other;
        return Objects.equals(lat, castOther.lat) && Objects.equals(lon, castOther.lon) && Objects.equals(name, castOther.name) &&Objects.equals(type, castOther.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon, name, type);
    }
}
