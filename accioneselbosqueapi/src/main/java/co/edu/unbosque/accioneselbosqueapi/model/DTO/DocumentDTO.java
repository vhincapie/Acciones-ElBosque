package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentDTO {

    @JsonProperty("document_type")
    private String documentType;
    @JsonProperty("document_sub_type")
    private String documentSubType;
    private String content;
    @JsonProperty("mime_type")
    private String mimeType;

    public DocumentDTO() {
    }

    public DocumentDTO(String documentType, String documentSubType, String content, String mimeType) {
        this.documentType = documentType;
        this.documentSubType = documentSubType;
        this.content = content;
        this.mimeType = mimeType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentSubType() {
        return documentSubType;
    }

    public void setDocumentSubType(String documentSubType) {
        this.documentSubType = documentSubType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
