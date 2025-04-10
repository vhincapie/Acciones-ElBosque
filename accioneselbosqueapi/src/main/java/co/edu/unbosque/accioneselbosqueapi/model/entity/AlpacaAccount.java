package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "alpaca_account")
public class AlpacaAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alpaca_account")
    private Long id;

    @Column(name = "alpaca_id")
    private String alpacaId;

    @Column(name = "status")
    private String status;

    @Column(name = "currency")
    private String currency;

    @Column(name = "account_type")
    private String accountType;

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AlpacaAccount() {
    }

    public AlpacaAccount(Long id, String alpacaId, String status, String currency, String accountType, User user, LocalDateTime createdAt) {
        this.id = id;
        this.alpacaId = alpacaId;
        this.status = status;
        this.currency = currency;
        this.accountType = accountType;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlpacaId() {
        return alpacaId;
    }

    public void setAlpacaId(String alpacaId) {
        this.alpacaId = alpacaId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
