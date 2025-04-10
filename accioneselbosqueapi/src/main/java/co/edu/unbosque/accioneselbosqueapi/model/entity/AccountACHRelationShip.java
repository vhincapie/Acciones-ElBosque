package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "AccountACHRelationship")
public class AccountACHRelationShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ach_id")
    private String achId;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "account_owner_name")
    private String accountOwnerName;

    @Column(name = "bank_account_type")
    private String bankAccountType;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "bank_routing_number")
    private String bankRoutingNumber;

    @Column(name = "nickname")
    private String nickname;

    public AccountACHRelationShip() {
    }

    public AccountACHRelationShip(String achId, String accountId, String accountOwnerName, String bankAccountType,
                                  String bankAccountNumber, String bankRoutingNumber, String nickname) {
        this.achId = achId;
        this.accountId = accountId;
        this.accountOwnerName = accountOwnerName;
        this.bankAccountType = bankAccountType;
        this.bankAccountNumber = bankAccountNumber;
        this.bankRoutingNumber = bankRoutingNumber;
        this.nickname = nickname;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAchId() {
        return achId;
    }

    public void setAchId(String achId) {
        this.achId = achId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
