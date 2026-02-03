package ru.geocommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.geocommerce.model.GeoRelevance;

import java.time.LocalDate;
import java.util.List;

public interface GeoRelevanceRepository extends JpaRepository<GeoRelevance, String> {
    @Transactional
    @Query("SELECT g FROM GeoRelevance g " +
            "WHERE g.osm_id = :osm_id " +
            "AND g.category = :category ")
    List<GeoRelevance> findInfo(
            @Param("osm_id") long osm_id,
            @Param("category") String category
    );

    @Transactional
    @Modifying
    @Query("UPDATE GeoRelevance t " +
            "SET t.updated = :updated " +
            "WHERE t.osm_id = :osm_id " +
            "AND t.category = :category")
    void updateInfo(
            @Param("updated") LocalDate updated,
            @Param("osm_id") long osm_id,
            @Param("category") String category
    );
}
