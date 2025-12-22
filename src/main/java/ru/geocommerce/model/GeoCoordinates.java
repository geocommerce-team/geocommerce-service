package ru.geocommerce.model;

public class GeoCoordinates {
    private double lat;
    private double lon;

    public GeoCoordinates() {}

    public GeoCoordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    // Getters & Setters
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
}