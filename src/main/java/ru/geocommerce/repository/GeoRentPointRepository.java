package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.model.GeoRentPoint;

import java.time.LocalDateTime;
import java.util.List;

public interface GeoRentPointRepository extends JpaRepository<GeoRentPoint, String> {
    @Transactional
    @Query("SELECT g FROM GeoRentPoint g " +
            "WHERE g.lat BETWEEN :latMin AND :latMax " +
            "AND g.lon BETWEEN :lonMin AND :lonMax")
    List<GeoRentPoint> findFreshByBounds(
            @Param("latMin") double latMin,
            @Param("lonMin") double lonMin,
            @Param("latMax") double latMax,
            @Param("lonMax") double lonMax
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM GeoRentPoint g " +
            "WHERE g.lat BETWEEN :latMin AND :latMax " +
            "AND g.lon BETWEEN :lonMin AND :lonMax")
    void deleteFreshByBounds(
            @Param("latMin") double latMin,
            @Param("latMax") double latMax,
            @Param("lonMin") double lonMin,
            @Param("lonMax") double lonMax
    );
}