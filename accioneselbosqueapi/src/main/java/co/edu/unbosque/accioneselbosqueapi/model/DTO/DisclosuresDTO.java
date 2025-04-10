package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DisclosuresDTO {

    @JsonProperty("is_control_person")
    private boolean controlPerson;

    @JsonProperty("is_affiliated_exchange_or_finra")
    private boolean affiliatedExchangeOrFinra;

    @JsonProperty("is_politically_exposed")
    private boolean politicallyExposed;

    @JsonProperty("immediate_family_exposed")
    private boolean immediateFamilyExposed;


    public DisclosuresDTO() {}

    public DisclosuresDTO(boolean controlPerson, boolean affiliatedExchangeOrFinra, boolean politicallyExposed, boolean immediateFamilyExposed) {
        this.controlPerson = controlPerson;
        this.affiliatedExchangeOrFinra = affiliatedExchangeOrFinra;
        this.politicallyExposed = politicallyExposed;
        this.immediateFamilyExposed = immediateFamilyExposed;
    }

    public boolean isControlPerson() {
        return controlPerson;
    }

    public void setControlPerson(boolean controlPerson) {
        this.controlPerson = controlPerson;
    }

    public boolean isAffiliatedExchangeOrFinra() {
        return affiliatedExchangeOrFinra;
    }

    public void setAffiliatedExchangeOrFinra(boolean affiliatedExchangeOrFinra) {
        this.affiliatedExchangeOrFinra = affiliatedExchangeOrFinra;
    }

    public boolean isPoliticallyExposed() {
        return politicallyExposed;
    }

    public void setPoliticallyExposed(boolean politicallyExposed) {
        this.politicallyExposed = politicallyExposed;
    }

    public boolean isImmediateFamilyExposed() {
        return immediateFamilyExposed;
    }

    public void setImmediateFamilyExposed(boolean immediateFamilyExposed) {
        this.immediateFamilyExposed = immediateFamilyExposed;
    }
}