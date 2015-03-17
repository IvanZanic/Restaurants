package ivan.rest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ivan on 12.03.15..
 */
public class Restaurant {

    private Integer id;
    private String name;
    private String address;
    private double longitude;
    private double latitude;
    private String photo;

    public Integer getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    @JsonProperty("Address")
    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    @JsonProperty("Longitude")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @JsonProperty("Latitude")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPhoto() {
        return photo;
    }

    @JsonIgnore
    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
