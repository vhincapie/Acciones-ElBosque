package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class WatchlistItemDTO {

    private Long id;
    private String symbol;
    private String companyName;
    private Double alertMinPrice;
    private Double alertMaxPrice;
    private Long user;
    private Double currentPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double previousClosePrice;

    public WatchlistItemDTO() {
    }

    public WatchlistItemDTO(Long id, String symbol, String companyName, Double alertMinPrice, Double alertMaxPrice, Long user, Double currentPrice, Double highPrice, Double lowPrice, Double previousClosePrice) {
        this.id = id;
        this.symbol = symbol;
        this.companyName = companyName;
        this.alertMinPrice = alertMinPrice;
        this.alertMaxPrice = alertMaxPrice;
        this.user = user;
        this.currentPrice = currentPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.previousClosePrice = previousClosePrice;
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

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(Double previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }
}
