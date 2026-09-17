package br.fai.lds.e_lixo_zero.dto;

public class PickupResponseDto {

    private int id;
    private String userName;
    private int collectorId;
    private String waste;
    private int quantity;
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String date;
    private String period;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public int getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(final int collectorId) {
        this.collectorId = collectorId;
    }

    public String getWaste() {
        return waste;
    }

    public void setWaste(final String waste) {
        this.waste = waste;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
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

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(final String period) {
        this.period = period;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
