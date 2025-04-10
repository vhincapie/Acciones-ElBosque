package co.edu.unbosque.accioneselbosqueapi.model.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CreateAccountRequestDTO {

    private ContactDTO contact;
    private IdentityDTO identity;
    private Long marketInterestId;
    private DisclosuresDTO disclosures;
    @JsonProperty("agreements")
    private List<AgreementDTO> agreements;
    private List<DocumentDTO> documents;
    @JsonProperty("trusted_contact")
    private TrustedContactDTO trustedContact;
    private String additionalInformation;
    private String accountType;
    private String password;
    @JsonProperty("enabled_assets")
    private List<String> enabledAssets;




    public CreateAccountRequestDTO() {
    }

    public CreateAccountRequestDTO(ContactDTO contact, IdentityDTO identity, Long marketInterestId, DisclosuresDTO disclosures, List<AgreementDTO> agreements, List<DocumentDTO> documents, TrustedContactDTO trustedContact, String additionalInformation, String accountType, String password, List<String> enabledAssets) {
        this.contact = contact;
        this.identity = identity;
        this.marketInterestId = marketInterestId;
        this.disclosures = disclosures;
        this.agreements = agreements;
        this.documents = documents;
        this.trustedContact = trustedContact;
        this.additionalInformation = additionalInformation;
        this.accountType = accountType;
        this.password = password;
        this.enabledAssets = enabledAssets;
    }

    public Long getMarketInterestId() {
        return marketInterestId;
    }

    public void setMarketInterestId(Long marketInterestId) {
        this.marketInterestId = marketInterestId;
    }

    public List<String> getEnabledAssets() {
        return enabledAssets;
    }
    public void setEnabledAssets(List<String> enabledAssets) {
        this.enabledAssets = enabledAssets;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ContactDTO getContact() {
        return contact;
    }

    public void setContact(ContactDTO contact) {
        this.contact = contact;
    }

    public IdentityDTO getIdentity() {
        return identity;
    }

    public void setIdentity(IdentityDTO identity) {
        this.identity = identity;
    }

    public DisclosuresDTO getDisclosures() {
        return disclosures;
    }

    public void setDisclosures(DisclosuresDTO disclosures) {
        this.disclosures = disclosures;
    }

    public List<AgreementDTO> getAgreements() {
        return agreements;
    }

    public void setAgreements(List<AgreementDTO> agreements) {
        this.agreements = agreements;
    }

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

    public TrustedContactDTO getTrustedContact() {
        return trustedContact;
    }

    public void setTrustedContact(TrustedContactDTO trustedContact) {
        this.trustedContact = trustedContact;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}