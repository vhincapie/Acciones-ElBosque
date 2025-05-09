package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "alpaca_account_id")
    private AlpacaAccount alpacaAccount;

    @ManyToOne
    @JoinColumn(name = "relationship_id")
    private AccountACHRelationShip accountACHRelationShip;

    @Column(name = "status")
    private String status;

    @Column(name = "alpaca_transfer_id")
    private String alpacaTransferId;



    public Transfer() {
    }

    public Transfer(Long id, LocalDateTime createdAt, String type, BigDecimal amount, String currency, AlpacaAccount alpacaAccount, AccountACHRelationShip accountACHRelationShip, String status, String alpacaTransferId) {
        this.id = id;
        this.createdAt = createdAt;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.alpacaAccount = alpacaAccount;
        this.accountACHRelationShip = accountACHRelationShip;
        this.status = status;
        this.alpacaTransferId = alpacaTransferId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AlpacaAccount getAlpacaAccount() {
        return alpacaAccount;
    }

    public void setAlpacaAccount(AlpacaAccount alpacaAccount) {
        this.alpacaAccount = alpacaAccount;
    }

    public AccountACHRelationShip getAccountACHRelationShip() {
        return accountACHRelationShip;
    }

    public void setAccountACHRelationShip(AccountACHRelationShip accountACHRelationShip) {
        this.accountACHRelationShip = accountACHRelationShip;
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

    public String getAlpacaTransferId() {
        return alpacaTransferId;
    }

    public void setAlpacaTransferId(String alpacaTransferId) {
        this.alpacaTransferId = alpacaTransferId;
    }
}
