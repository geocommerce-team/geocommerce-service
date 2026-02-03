package ru.geocommerce.model;

import java.io.Serializable;
import java.util.Objects;

public class GeoRelevanceId implements Serializable {
    private String category;
    private long osm_id;

    public GeoRelevanceId() {}

    public GeoRelevanceId(String category, long place_id) {
        this.category = category;
        this.osm_id = place_id;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GeoRelevanceId)) return false;
        GeoRelevanceId castOther = (GeoRelevanceId) other;
        return Objects.equals(osm_id, castOther.osm_id) && Objects.equals(category, castOther.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, osm_id);
    }
}
