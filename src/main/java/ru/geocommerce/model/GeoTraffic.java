package ru.geocommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "geo_traffic")
@IdClass(GeoTrafficId.class)
public class GeoTraffic {
    @Id
    @Column(name = "lat")
    private int lat;
    @Id
    @Column(name = "lon")
    private int lon;
    @Column(name = "count_people")
    private int count_people;

    public GeoTraffic() {}

    public GeoTraffic(int lat, int lon, int count_people) {
        this.lat = lat;
        this.lon = lon;
        this.count_people = count_people;
    }

    public int getLat() { return lat; }
    public void setLat(int lat) { this.lat = lat; }
    public int getLon() { return lon; }
    public void setLon(int lon) { this.lon = lon; }
    public int getCount_people() { return count_people; }
    public void setCount_people(int count_people) { this.count_people = count_people; }
}