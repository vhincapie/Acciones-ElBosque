package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;

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
    private String amount;

    @Column(name = "currency")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "alpaca_account_id")
    private AlpacaAccount alpacaAccount;

    @ManyToOne
    @JoinColumn(name = "relationship_id")
    private AccountACHRelationShip accountACHRelationShip;

    public Transfer() {
    }

    public Transfer(LocalDateTime createdAt, String type, String amount, String currency, AlpacaAccount alpacaAccount, AccountACHRelationShip accountACHRelationShip) {
        this.createdAt = createdAt;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.alpacaAccount = alpacaAccount;
        this.accountACHRelationShip = accountACHRelationShip;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
}
