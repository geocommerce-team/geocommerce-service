package ru.geocommerce.model;

import lombok.Getter;

public class GeoRegion {
    @Getter
    public final double minLat;
    @Getter
    public final double minLon;
    @Getter
    public final double maxLat;
    @Getter
    public final double maxLon;
    @Getter
    public final long osm_id;

    public GeoRegion(double minLat, double minLon, double maxLat, double maxLon, long osm_id) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        this.osm_id = osm_id;
    }

    @Override
    public String toString() {
        return "GeoRectangle{" +
                "minLat=" + minLat +
                ", minLon=" + minLon +
                ", maxLat=" + maxLat +
                ", maxLon=" + maxLon +
                '}';
    }
}
