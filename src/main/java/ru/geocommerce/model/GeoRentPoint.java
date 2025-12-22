package ru.geocommerce.model;

public class GeoRentPoint {
    private String id;
    private double lat;
    private double lon;
    private String name;
    private String type;

    // Конструкторы, геттеры, сеттеры
    public GeoRentPoint() {}

    public GeoRentPoint(String id, double lat, double lon, String name, String type) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.type = type;
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
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}