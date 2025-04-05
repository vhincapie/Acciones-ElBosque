package co.edu.unbosque.accioneselbosqueapi.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_item")
public class WatchlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_watchlist")
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "alert_min_price")
    private Double alertMinPrice;

    @Column(name = "alert_max_price")
    private Double alertMaxPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public WatchlistItem() {
    }

    public WatchlistItem(String symbol, String companyName, Double alertMinPrice, Double alertMaxPrice, LocalDateTime createdAt) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.alertMinPrice = alertMinPrice;
        this.alertMaxPrice = alertMaxPrice;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getAlertMinPrice() {
        return alertMinPrice;
    }

    public void setAlertMinPrice(Double alertMinPrice) {
        this.alertMinPrice = alertMinPrice;
    }

    public Double getAlertMaxPrice() {
        return alertMaxPrice;
    }

    public void setAlertMaxPrice(Double alertMaxPrice) {
        this.alertMaxPrice = alertMaxPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
