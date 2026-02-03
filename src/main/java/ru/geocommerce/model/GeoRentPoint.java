package ru.geocommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geo_rent_points")
@IdClass(GeoRentPointId.class)
public class GeoRentPoint {
    @Id
    @Column(name = "lat")
    private double lat;
    @Id
    @Column(name = "lon")
    private double lon;
    @Id
    @Column(name = "name")
    private String name;
    @Id
    @Column(name = "type")
    private String type;

    public GeoRentPoint() {}

    public GeoRentPoint(double lat, double lon, String name, String type) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.type = type;
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}