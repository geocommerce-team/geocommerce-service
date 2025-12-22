package ru.geocommerce.model;

public class GeoRetailPoint {
    private String id;
    private double lat;
    private double lon;
    private String name;
    private String category;

    // Конструкторы, геттеры, сеттеры
    public GeoRetailPoint() {}

    public GeoRetailPoint(String id, double lat, double lon, String name, String category) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.category = category;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}