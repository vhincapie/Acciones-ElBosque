package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseAccountACHDTO {

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

    @JsonProperty("account_owner_name")
    private String accountOwnerName;

    @JsonProperty("bank_account_type")
    private String bankAccountType;

    @JsonProperty("bank_account_number")
    private String bankAccountNumber;

    @JsonProperty("bank_routing_number")
    private String bankRoutingNumber;

    @JsonProperty("nickname")
    private String nickname;

    public ResponseAccountACHDTO() {
    }

    public ResponseAccountACHDTO(String id, String createdAt, String updatedAt, String accountId, String status,
                                 String accountOwnerName, String bankAccountType, String bankAccountNumber,
                                 String bankRoutingNumber, String nickname) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountId = accountId;
        this.status = status;
        this.accountOwnerName = accountOwnerName;
        this.bankAccountType = bankAccountType;
        this.bankAccountNumber = bankAccountNumber;
        this.bankRoutingNumber = bankRoutingNumber;
        this.nickname = nickname;
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

    public String getAccountOwnerName() {
        return accountOwnerName;
    }

    public void setAccountOwnerName(String accountOwnerName) {
        this.accountOwnerName = accountOwnerName;
    }

    public String getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
