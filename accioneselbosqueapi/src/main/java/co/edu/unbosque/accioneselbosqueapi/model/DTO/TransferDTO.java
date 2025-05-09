package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferDTO {

    @JsonProperty("transfer_type")
    private String transferType;

    @JsonProperty("relationship_id")
    private String relationshipId;

    @JsonProperty("bank_id")
    private String bankId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("timing")
    private String timing;

    private Long alpacaAccountId;

    private Long accountAchrelationshipId;

    private String status;

    private String alpacaTransferId;

    public TransferDTO() {}

    public TransferDTO(String transferType, String relationshipId, String bankId, BigDecimal amount, String direction, String timing, Long alpacaAccountId, Long accountAchrelationshipId, String status, String alpacaTransferId) {
        this.transferType = transferType;
        this.relationshipId = relationshipId;
        this.bankId = bankId;
        this.amount = amount;
        this.direction = direction;
        this.timing = timing;
        this.alpacaAccountId = alpacaAccountId;
        this.accountAchrelationshipId = accountAchrelationshipId;
        this.status = status;
        this.alpacaTransferId = alpacaTransferId;
    }

    // Getters y Setters
    public String getAlpacaTransferId() {
        return alpacaTransferId;
    }

    public void setAlpacaTransferId(String alpacaTransferId) {
        this.alpacaTransferId = alpacaTransferId;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
