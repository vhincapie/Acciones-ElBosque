package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class AlpacaAccountResponseDTO {
    private String id;
    private String status;
    private String currency;
    private String accountType;

    public AlpacaAccountResponseDTO() {
    }

    public AlpacaAccountResponseDTO(String id, String status, String currency, String accountType) {
        this.id = id;
        this.status = status;
        this.currency = currency;
        this.accountType = accountType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
