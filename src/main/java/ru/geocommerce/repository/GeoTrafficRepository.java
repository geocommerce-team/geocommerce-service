package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.geocommerce.model.GeoRentPoint;
import ru.geocommerce.model.GeoTraffic;

import java.time.LocalDateTime;
import java.util.List;


public interface GeoTrafficRepository  extends JpaRepository<GeoTraffic, String> {
    @Query("SELECT g FROM Population g " +
            "WHERE g.lat = :lat " +
            "AND g.lon = :lon " +
            "AND g.lastUpdated > :threshold")
    List<GeoTraffic> findFreshByBounds(
            @Param("id") double lat,
            @Param("lon") double lon,
            @Param("threshold") LocalDateTime threshold
    );
}
