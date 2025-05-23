package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;


import java.math.BigDecimal;

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

    @OneToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "buying_power")
    private BigDecimal buyingPower;

    public AlpacaAccount() {}

    public AlpacaAccount(Long id, String alpacaId, String status, String currency, User user, LocalDateTime createdAt, BigDecimal buyingPower) {
        this.id = id;
        this.alpacaId = alpacaId;
        this.status = status;
        this.currency = currency;
        this.user = user;
        this.createdAt = createdAt;
        this.buyingPower = buyingPower;
    }

    // Getters y setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getAlpacaId() { return alpacaId; }

    public void setAlpacaId(String alpacaId) { this.alpacaId = alpacaId; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BigDecimal getBuyingPower() { return buyingPower; }

    public void setBuyingPower(BigDecimal buyingPower) { this.buyingPower = buyingPower; }
}
