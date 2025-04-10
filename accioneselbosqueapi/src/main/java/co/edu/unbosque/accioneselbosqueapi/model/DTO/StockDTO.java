package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class StockDTO {
    private String symbol;
    private String description;
    private double currentPrice;
    private double highPrice;
    private double lowPrice;
    private double previousClosePrice;

    public StockDTO() {

    }

    public StockDTO(String symbol, String description, double currentPrice, double highPrice, double lowPrice, double previousClosePrice) {
        this.symbol = symbol;
        this.description = description;
        this.currentPrice = currentPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.previousClosePrice = previousClosePrice;
    }


    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(double previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }
}