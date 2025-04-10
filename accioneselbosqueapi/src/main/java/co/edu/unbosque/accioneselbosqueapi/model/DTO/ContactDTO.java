package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ContactDTO {

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("street_address")
    private List<String> streetAddress;

    private String city;

    @JsonProperty("postal_code")
    private String postalCode;

    private String state;
    private String country;

    public ContactDTO() {}

    public ContactDTO(String country, String state, String postalCode, String city, List<String> streetAddress,
                      String phoneNumber, String emailAddress, String familyName, String givenName) {
        this.country = country;
        this.state = state;
        this.postalCode = postalCode;
        this.city = city;
        this.streetAddress = streetAddress;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.familyName = familyName;
        this.givenName = givenName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(List<String> streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
