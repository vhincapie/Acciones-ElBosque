package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ACHRelationShipDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("bank_code")
    private String bankCode;

    @JsonProperty("bank_code_type")
    private String bankCodeType;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("country")
    private String country;

    @JsonProperty("state_province")
    private String stateProvince;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("city")
    private String city;

    @JsonProperty("street_address")
    private String streetAddress;

    public ACHRelationShipDTO() {
    }

    public ACHRelationShipDTO(String name, String bankCode, String bankCodeType, String accountNumber,
                              String country, String stateProvince, String postalCode, String city, String streetAddress) {
        this.name = name;
        this.bankCode = bankCode;
        this.bankCodeType = bankCodeType;
        this.accountNumber = accountNumber;
        this.country = country;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.city = city;
        this.streetAddress = streetAddress;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankCodeType() {
        return bankCodeType;
    }

    public void setBankCodeType(String bankCodeType) {
        this.bankCodeType = bankCodeType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
}
