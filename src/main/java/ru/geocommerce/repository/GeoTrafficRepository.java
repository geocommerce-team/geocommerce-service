package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.model.GeoTraffic;

import java.time.LocalDateTime;
import java.util.List;


public interface GeoTrafficRepository  extends JpaRepository<GeoTraffic, String> {
    @Transactional
    @Query("SELECT g FROM GeoTraffic g " +
            "WHERE g.lat = :lat " +
            "AND g.lon = :lon ")
    List<GeoTraffic> findFreshByBounds(
            @Param("lat") int lat,
            @Param("lon") int lon
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM GeoTraffic g " +
            "WHERE g.lat BETWEEN :latMin AND :latMax " +
            "AND g.lon BETWEEN :lonMin AND :lonMax")
    void deleteFreshByBounds(
            @Param("latMin") int latMin,
            @Param("latMax") int latMax,
            @Param("lonMin") int lonMin,
            @Param("lonMax") int lonMax
    );
}
