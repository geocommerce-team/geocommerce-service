package ru.geocommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "geo_retail_points_density")
@IdClass(GeoRetailDensityId.class)
public class GeoRetailDensity {
    @Id
    @Column(name = "category")
    private String category;
    @Id
    @Column(name = "lat")
    private int lat;
    @Id
    @Column(name = "lon")
    private int lon;
    @Column(name = "count_points")
    private int count_points;

    public GeoRetailDensity() {}

    public GeoRetailDensity(String category, int lat, int lon, int count_points) {
        this.category = category;
        this.lat = lat;
        this.lon = lon;
        this.count_points = count_points;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getLat() { return lat; }
    public void setLat(int lat) { this.lat = lat; }
    public int getLon() { return lon; }
    public void setLon(int lon) { this.lon = lon; }
    public int getCount_points() { return count_points; }
    public void setCount_points(int count_points) { this.count_points = count_points; }
}
