package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AgreementDTO {

    private String agreement;

    @JsonProperty("signed_at")
    private String signedAt;

    @JsonProperty("ip_address")
    private String ipAddress;

    public AgreementDTO() {
    }

    public AgreementDTO(String agreement, LocalDateTime signedAt, String ipAddress) {
        this.agreement = agreement;
        this.signedAt = signedAt.format(DateTimeFormatter.ISO_DATE_TIME) + "Z";
        this.ipAddress = ipAddress;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public String getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(String signedAt) {
        this.signedAt = signedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
