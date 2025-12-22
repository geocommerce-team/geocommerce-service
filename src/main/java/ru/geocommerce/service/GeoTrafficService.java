package ru.geocommerce.service;

import org.springframework.stereotype.Service;
import ru.geocommerce.extern.client.PopulationClient;

@Service
public class GeoTrafficService {

    private final PopulationClient populationClient;

    public GeoTrafficService(PopulationClient populationClient) {
        this.populationClient = populationClient;
    }

    public int getPopulation(double latMin, double lonMin, double latMax, double lonMax) {
        return populationClient.getPopulation(latMin, lonMin, latMax, lonMax);
    }
}