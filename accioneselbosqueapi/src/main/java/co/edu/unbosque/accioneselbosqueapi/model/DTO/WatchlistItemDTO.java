package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class WatchlistItemDTO {

    private Long id;
    private String symbol;
    private String companyName;
    private Double alertMinPrice;
    private Double alertMaxPrice;

    public WatchlistItemDTO() {
    }

    public WatchlistItemDTO(Long id, String symbol, String companyName, Double alertMinPrice, Double alertMaxPrice) {
        this.id = id;
        this.symbol = symbol;
        this.companyName = companyName;
        this.alertMinPrice = alertMinPrice;
        this.alertMaxPrice = alertMaxPrice;
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
}
