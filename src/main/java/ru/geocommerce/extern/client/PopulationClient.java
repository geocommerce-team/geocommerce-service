package ru.geocommerce.extern.client;

public interface PopulationClient {
    int getPopulation(double latMin, double lonMin, double latMax, double lonMax);
}