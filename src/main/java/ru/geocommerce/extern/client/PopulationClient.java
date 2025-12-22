package ru.geocommerce.extern.client;

import ru.geocommerce.extern.dto.PopulationResponse;

public interface PopulationClient {
    int getPopulation(double latMin, double lonMin, double latMax, double lonMax);
}