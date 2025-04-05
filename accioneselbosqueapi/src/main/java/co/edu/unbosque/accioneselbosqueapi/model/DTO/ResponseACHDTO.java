package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseACHDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("name")
    private String name;

    @JsonProperty("bank_code")
    private String bankCode;

    @JsonProperty("bank_code_type")
    private String bankCodeType;

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

    @JsonProperty("account_number")
    private String accountNumber;

    public ResponseACHDTO() {
    }

    public ResponseACHDTO(String id, String createdAt, String updatedAt, String accountId, String status, String name, String bankCode, String bankCodeType, String country, String stateProvince, String postalCode, String city, String streetAddress, String accountNumber) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountId = accountId;
        this.status = status;
        this.name = name;
        this.bankCode = bankCode;
        this.bankCodeType = bankCodeType;
        this.country = country;
        this.stateProvince = stateProvince;
        this.postalCode = postalCode;
        this.city = city;
        this.streetAddress = streetAddress;
        this.accountNumber = accountNumber;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
