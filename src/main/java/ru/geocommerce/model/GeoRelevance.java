package ru.geocommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "table_relevance")
@IdClass(GeoRelevanceId.class)
public class GeoRelevance {
    @Getter @Setter
    @Column(name = "lat_min")
    private int lat_min;
    @Getter @Setter
    @Column(name = "lat_max")
    private int lat_max;
    @Getter @Setter
    @Column(name = "lon_min")
    private int lon_min;
    @Getter @Setter
    @Column(name = "lon_max")
    private int lon_max;
    @Id
    @Getter @Setter
    @Column(name = "osm_id")
    private long osm_id;
    @Id
    @Getter @Setter
    @Column(name = "category")
    private String category;
    @Getter @Setter
    @Column(name = "updated")
    private LocalDate updated;

    public GeoRelevance() {}

    public GeoRelevance(int lat_min, int lat_max, int lon_min, int lon_max, long osm_id, String category) {
        this.lat_min = lat_min;
        this.lat_max = lat_max;
        this.lon_min = lon_min;
        this.lon_max = lon_max;
        this.osm_id = osm_id;
        this.category = category;
        this.updated = LocalDate.now();
    }
}
