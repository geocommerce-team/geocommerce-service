package ru.geocommerce.extern.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {
    @Getter
    @Setter
    private long place_id;
    @Getter
    @Setter
    private long osm_id;
    @Getter
    @Setter
    private String lat;
    @Getter
    @Setter
    private String lon;
    @Getter
    @Setter
    private String display_name;
    @Getter
    @Setter
    private List<String> boundingbox;
    @Getter
    @Setter
    private Address address;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        public String city;
        public String town;
        public String village;
        public String state;
        public String country;
        public String country_code;
    }
}
