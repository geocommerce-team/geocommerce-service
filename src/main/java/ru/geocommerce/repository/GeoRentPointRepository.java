package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.geocommerce.model.GeoRentPoint;

import java.time.LocalDateTime;
import java.util.List;

public interface GeoRentPointRepository extends JpaRepository<GeoRentPoint, String> {

    @Query("SELECT g FROM GeoRentPoint g " +
            "WHERE g.lat BETWEEN :bottom AND :top " +
            "AND g.lon BETWEEN :left AND :right " +
            "AND g.lastUpdated > :threshold")
    List<GeoRentPoint> findFreshByBounds(
            @Param("left") double left,
            @Param("right") double right,
            @Param("top") double top,
            @Param("bottom") double bottom,
            @Param("threshold") LocalDateTime threshold
    );
}