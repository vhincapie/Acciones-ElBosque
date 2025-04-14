package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequestDTO {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("qty")
    private String qty;

    @JsonProperty("side")
    private String side; // buy o sell

    @JsonProperty("type")
    private String type; // market, limit, stop, stop_limit

    @JsonProperty("time_in_force")
    private String timeInForce; // day

    @JsonProperty("limit_price")
    private String limitPrice;

    @JsonProperty("stop_price")
    private String stopPrice;

    private Long alpacaAccountId;

    public OrderRequestDTO() {}

    public OrderRequestDTO(String symbol, String qty, String side, String type, String timeInForce, String limitPrice, String stopPrice, Long alpacaAccountId) {
        this.symbol = symbol;
        this.qty = qty;
        this.side = side;
        this.type = type;
        this.timeInForce = timeInForce;
        this.limitPrice = limitPrice;
        this.stopPrice = stopPrice;
        this.alpacaAccountId = alpacaAccountId;
    }

    // Getters y setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public String getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(String limitPrice) {
        this.limitPrice = limitPrice;
    }

    public String getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(String stopPrice) {
        this.stopPrice = stopPrice;
    }

    public Long getAlpacaAccountId() {
        return alpacaAccountId;
    }

    public void setAlpacaAccountId(Long alpacaAccountId) {
        this.alpacaAccountId = alpacaAccountId;
    }
}
