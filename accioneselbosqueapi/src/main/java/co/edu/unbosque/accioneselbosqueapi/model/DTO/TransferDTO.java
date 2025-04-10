package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferDTO {

    @JsonProperty("transfer_type")
    private String transferType;

    @JsonProperty("relationship_id")
    private String relationshipId;

    @JsonProperty("bank_id")
    private String bankId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("timing")
    private String timing;

    public TransferDTO() {}

    public TransferDTO(String transferType, String relationshipId, String bankId, String amount, String direction, String timing) {
        this.transferType = transferType;
        this.relationshipId = relationshipId;
        this.bankId = bankId;
        this.amount = amount;
        this.direction = direction;
        this.timing = timing;
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
}
