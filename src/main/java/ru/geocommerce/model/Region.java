package ru.geocommerce.model;

import lombok.Getter;
import lombok.Setter;

public class Region {
    @Setter
    @Getter
    private double latMin;
    @Setter
    @Getter
    private double latMax;
    @Setter
    @Getter
    private double lonMin;
    @Setter
    @Getter
    private double lonMax;
    @Setter
    @Getter
    private int population;
    @Setter
    @Getter
    private int countRetailPoints;
    @Setter
    @Getter
    private double potential;
    @Setter
    @Getter
    private int zoneId = -1;

    public Region(double latMin, double latMax,
                  double lonMin, double lonMax,
                  int population, int countRetailPoints,
                  double potential, int zoneId) {
        this.latMin = latMin;
        this.latMax = latMax;
        this.lonMin = lonMin;
        this.lonMax = lonMax;
        this.population = population;
        this.countRetailPoints = countRetailPoints;
        this.potential = potential;
        this.zoneId = zoneId;
    }
}
