package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "asset_class")
    private String assetClass;

    @Column(name = "qty")
    private BigDecimal qty;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "side")
    private String side;

    @Column(name = "time_in_force")
    private String timeInForce;

    @Column(name = "limit_price")
    private BigDecimal limitPrice;

    @Column(name = "stop_price")
    private BigDecimal stopPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    @Column(name = "commission_amount")
    private BigDecimal commissionAmount;

    @Column(name = "net_amount")
    private BigDecimal netAmount;

    @ManyToOne
    @JoinColumn(name = "alpaca_account_id")
    private AlpacaAccount alpacaAccount;

    @Column(name = "alpaca_order_id")
    private String alpacaOrderId;

    public Order() {}

    public Order(Long id, LocalDateTime createdAt, String symbol, String assetClass, BigDecimal qty, String orderType, String side, String timeInForce, BigDecimal limitPrice, BigDecimal stopPrice, String status, BigDecimal transactionAmount, BigDecimal commissionAmount, BigDecimal netAmount, AlpacaAccount alpacaAccount, String alpacaOrderId) {
        this.id = id;
        this.createdAt = createdAt;
        this.symbol = symbol;
        this.assetClass = assetClass;
        this.qty = qty;
        this.orderType = orderType;
        this.side = side;
        this.timeInForce = timeInForce;
        this.limitPrice = limitPrice;
        this.stopPrice = stopPrice;
        this.status = status;
        this.transactionAmount = transactionAmount;
        this.commissionAmount = commissionAmount;
        this.netAmount = netAmount;
        this.alpacaAccount = alpacaAccount;
        this.alpacaOrderId = alpacaOrderId;
    }

    // Getters y setters
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public AlpacaAccount getAlpacaAccount() {
        return alpacaAccount;
    }

    public void setAlpacaAccount(AlpacaAccount alpacaAccount) {
        this.alpacaAccount = alpacaAccount;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public BigDecimal getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(BigDecimal stopPrice) {
        this.stopPrice = stopPrice;
    }

    public String getAlpacaOrderId() {
        return alpacaOrderId;
    }

    public void setAlpacaOrderId(String alpacaOrderId) {
        this.alpacaOrderId = alpacaOrderId;
    }
}
