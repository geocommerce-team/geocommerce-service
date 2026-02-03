package ru.geocommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geo_retail_points")
@IdClass(GeoRetailPointId.class)
public class GeoRetailPoint {
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
    @Column(name = "category")
    private String category;

    public GeoRetailPoint() {}

    public GeoRetailPoint(double lat, double lon, String name, String category) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.category = category;
    }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}