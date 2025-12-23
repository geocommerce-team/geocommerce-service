package ru.geocommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geo_traffic")
public class GeoTraffic {

    @Id
    private String id;
    private double lat;
    private double lon;
    private int count_people;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public GeoTraffic() {}

    public GeoTraffic(String id, double lat, double lon, int count_people) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.count_people = count_people;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
    public int getCount_people() { return count_people; }
    public void setCount_people(int count_people) { this.count_people = count_people; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}