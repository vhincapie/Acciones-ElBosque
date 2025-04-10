package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlpacaAccountRequestDTO {

    private ContactDTO contact;
    private IdentityDTO identity;
    private DisclosuresDTO disclosures;
    private List<AgreementDTO> agreements;
    private List<DocumentDTO> documents;

    @JsonProperty("trusted_contact")
    private TrustedContactDTO trustedContact;

    @JsonProperty("enabled_assets")
    private List<String> enabledAssets;

    public AlpacaAccountRequestDTO() {
    }

    public AlpacaAccountRequestDTO(ContactDTO contact, IdentityDTO identity, DisclosuresDTO disclosures, List<AgreementDTO> agreements, List<DocumentDTO> documents, TrustedContactDTO trustedContact, List<String> enabledAssets) {
        this.contact = contact;
        this.identity = identity;
        this.disclosures = disclosures;
        this.agreements = agreements;
        this.documents = documents;
        this.trustedContact = trustedContact;
        this.enabledAssets = enabledAssets;
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

    public List<String> getEnabledAssets() {
        return enabledAssets;
    }

    public void setEnabledAssets(List<String> enabledAssets) {
        this.enabledAssets = enabledAssets;
    }
}
