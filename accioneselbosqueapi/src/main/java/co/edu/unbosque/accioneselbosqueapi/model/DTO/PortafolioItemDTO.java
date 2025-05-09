package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import java.math.BigDecimal;

public class PortafolioItemDTO {

    private String symbol;
    private int totalQty;
    private int totalVendidas;
    private BigDecimal averageBuyPrice;
    private BigDecimal averageSellPrice;
    private String side;

    public PortafolioItemDTO() {}

    public PortafolioItemDTO(String symbol, int totalQty, int totalVendidas, BigDecimal averageBuyPrice, BigDecimal averageSellPrice, String side) {
        this.symbol = symbol;
        this.totalQty = totalQty;
        this.totalVendidas = totalVendidas;
        this.averageBuyPrice = averageBuyPrice;
        this.averageSellPrice = averageSellPrice;
        this.side = side;
    }

    //Getters y Setters

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getTotalVendidas() {
        return totalVendidas;
    }

    public void setTotalVendidas(int totalVendidas) {
        this.totalVendidas = totalVendidas;
    }

    public BigDecimal getAverageBuyPrice() {
        return averageBuyPrice;
    }

    public void setAverageBuyPrice(BigDecimal averageBuyPrice) {
        this.averageBuyPrice = averageBuyPrice;
    }

    public BigDecimal getAverageSellPrice() {
        return averageSellPrice;
    }

    public void setAverageSellPrice(BigDecimal averageSellPrice) {
        this.averageSellPrice = averageSellPrice;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
