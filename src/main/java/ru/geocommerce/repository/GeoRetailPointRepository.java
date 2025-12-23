package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.geocommerce.model.GeoRetailPoint;

import java.time.LocalDateTime;
import java.util.List;

public interface GeoRetailPointRepository extends JpaRepository<GeoRetailPoint, String> {

    @Query("SELECT g FROM GeoRetailPoint g " +
            "WHERE g.category = :category " +
            "AND g.lat BETWEEN :latMin AND :latMax " +
            "AND g.lon BETWEEN :lonMin AND :lonMax " +
            "AND g.lastUpdated > :threshold")
    List<GeoRetailPoint> findFreshByBoundsAndCategory(
            @Param("category") String category,
            @Param("latMin") double latMin,
            @Param("latMax") double latMax,
            @Param("lonMin") double lonMin,
            @Param("lonMax") double lonMax,
            @Param("threshold") LocalDateTime threshold
    );
}