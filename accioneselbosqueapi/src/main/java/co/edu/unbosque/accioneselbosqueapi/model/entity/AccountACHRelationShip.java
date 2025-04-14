package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_ach_relationship")
public class AccountACHRelationShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "ach_id")
    private String achId;

    @Column(name = "account_owner_name")
    private String accountOwnerName;

    @Column(name = "bank_account_type")
    private String bankAccountType;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "nickname")
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "alpaca_account_id")
    private AlpacaAccount alpacaAccount;

    public AccountACHRelationShip() {
    }

    public AccountACHRelationShip(Long id, LocalDateTime createdAt, String achId, String accountOwnerName, String bankAccountType, String bankAccountNumber, String nickname, AlpacaAccount alpacaAccount) {
        this.id = id;
        this.createdAt = createdAt;
        this.achId = achId;
        this.accountOwnerName = accountOwnerName;
        this.bankAccountType = bankAccountType;
        this.bankAccountNumber = bankAccountNumber;
        this.nickname = nickname;
        this.alpacaAccount = alpacaAccount;
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

    public String getAchId() {
        return achId;
    }

    public void setAchId(String achId) {
        this.achId = achId;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public AlpacaAccount getAlpacaAccount() {
        return alpacaAccount;
    }

    public void setAlpacaAccount(AlpacaAccount alpacaAccount) {
        this.alpacaAccount = alpacaAccount;
    }
}
