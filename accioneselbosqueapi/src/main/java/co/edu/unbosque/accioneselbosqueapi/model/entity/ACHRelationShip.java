package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ACHRelationship")
public class ACHRelationShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ach_id")
    private String achId;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "status")
    private String status;

    @Column(name = "name")
    private String name;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "bank_code_type")
    private String bankCodeType;

    @Column(name = "country")
    private String country;

    @Column(name = "state_province")
    private String stateProvince;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "account_number")
    private String accountNumber;

    public ACHRelationShip() {

    }

    public ACHRelationShip(String achId, String createdAt, String updatedAt, String accountId, String status, String name, String bankCode, String bankCodeType, String country, String stateProvince, String postalCode, String city, String streetAddress, String accountNumber) {
        this.achId = achId;
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAchId() {
        return achId;
    }

    public void setAchId(String achId) {
        this.achId = achId;
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
