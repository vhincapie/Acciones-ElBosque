package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import java.time.LocalDateTime;

public class AlpacaAccountUserResponseDTO {

    private String alpacaId;
    private String status;
    private String currency;
    private String accountType;
    private LocalDateTime createdAt;

    public AlpacaAccountUserResponseDTO() {}

    public AlpacaAccountUserResponseDTO(String alpacaId, String status, String currency, String accountType, LocalDateTime createdAt) {
        this.alpacaId = alpacaId;
        this.status = status;
        this.currency = currency;
        this.accountType = accountType;
        this.createdAt = createdAt;
    }



    public String getAlpacaId() { return alpacaId; }
    public void setAlpacaId(String alpacaId) { this.alpacaId = alpacaId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
