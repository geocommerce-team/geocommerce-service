package ru.geocommerce.extern.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class RentPointDto {
    private String id;
    private double latitude;
    private double longitude;
    private int radius;
    private String pointType; // или просто type

    // Конструктор по умолчанию (обязателен для Jackson)
    public RentPointDto() {}

    // Конструктор для удобства (опционально)
    public RentPointDto(String id, double latitude, double longitude, int radius, String pointType) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.pointType = pointType;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    @Override
    public String toString() {
        return "RentPointDto{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", pointType='" + pointType + '\'' +
                '}';
    }
}