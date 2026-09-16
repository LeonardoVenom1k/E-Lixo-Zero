package br.fai.lds.e_lixo_zero.domain;

import java.util.ArrayList;
import java.util.List;

public class CollectionPointModel {

    private int id;
    private String name;
    private String address;
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String phone;
    private String openingHours;
    private List<String> acceptedWastes = new ArrayList<>();
    private double latitude;
    private double longitude;
    private boolean active;
    private Double distanceKm;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAddress() {
        if (address != null && !address.isBlank()) {
            return address;
        }
        final StringBuilder sb = new StringBuilder();
        if (street != null && !street.isBlank()) {
            sb.append(street);
        }
        if (number != null && !number.isBlank()) {
            sb.append(", ").append(number);
        }
        if (neighborhood != null && !neighborhood.isBlank()) {
            sb.append(" - ").append(neighborhood);
        }
        if (city != null && !city.isBlank()) {
            sb.append(", ").append(city);
        }
        if (state != null && !state.isBlank()) {
            sb.append(" - ").append(state);
        }
        return sb.toString();
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(final String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(final String openingHours) {
        this.openingHours = openingHours;
    }

    public List<String> getAcceptedWastes() {
        return acceptedWastes;
    }

    public void setAcceptedWastes(final List<String> acceptedWastes) {
        this.acceptedWastes = acceptedWastes != null ? acceptedWastes : new ArrayList<>();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(final Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}
