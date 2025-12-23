package ru.geocommerce.extern.dto;

public record RetailPointDto(
        Long id,
        Double lat,
        Double lon,
        String name
) {}