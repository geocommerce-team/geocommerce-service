package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.model.GeoRetailDensity;
import ru.geocommerce.model.GeoTraffic;

import java.time.LocalDateTime;
import java.util.List;

public interface GeoRetailPointsDensityRepository extends JpaRepository<GeoRetailDensity, String> {
    @Transactional
    @Query("SELECT g FROM GeoRetailDensity g " +
            "WHERE g.category = :category " +
            "AND g.lat = :lat " +
            "AND g.lon = :lon ")
    List<GeoRetailDensity> findFreshByBoundsAndCategory(
            @Param("category") String category,
            @Param("lat") int lat,
            @Param("lon") int lon
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM GeoRetailDensity g " +
            "WHERE g.lat BETWEEN :latMin AND :latMax " +
            "AND g.lon BETWEEN :lonMin AND :lonMax " +
            "AND g.category = :category")
    void deleteFreshByBoundsAndCategory(
            @Param("latMin") double latMin,
            @Param("latMax") double latMax,
            @Param("lonMin") double lonMin,
            @Param("lonMax") double lonMax,
            @Param("category") String category
    );
}
