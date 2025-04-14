package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferDTO {

    @JsonProperty("transfer_type")
    private String transferType;

    @JsonProperty("relationship_id")
    private String relationshipId; // Solo para ACH

    @JsonProperty("bank_id")
    private String bankId; // Solo para wire

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("timing")
    private String timing; // Solo aplica "immediate" por ahora

    private Long alpacaAccountId;

    private Long accountAchrelationshipId;

    public TransferDTO() {}

    public TransferDTO(String transferType, String relationshipId, String bankId, String amount, String direction, String timing, Long alpacaAccountId, Long accountAchrelationshipId) {
        this.transferType = transferType;
        this.relationshipId = relationshipId;
        this.bankId = bankId;
        this.amount = amount;
        this.direction = direction;
        this.timing = timing;
        this.alpacaAccountId = alpacaAccountId;
        this.accountAchrelationshipId = accountAchrelationshipId;
    }

    // Getters y Setters

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public Long getAlpacaAccountId() {
        return alpacaAccountId;
    }

    public void setAlpacaAccountId(Long alpacaAccountId) {
        this.alpacaAccountId = alpacaAccountId;
    }

    public Long getAccountAchrelationshipId() {
        return accountAchrelationshipId;
    }

    public void setAccountAchrelationshipId(Long accountAchrelationshipId) {
        this.accountAchrelationshipId = accountAchrelationshipId;
    }
}
