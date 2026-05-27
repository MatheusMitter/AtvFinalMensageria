package com.atvfinal.mensageria.dto;

import jakarta.validation.constraints.NotBlank;

public class EmailMensagemDTO {

    @NotBlank
    private String assunto;

    @NotBlank
    private String corpo;

    public EmailMensagemDTO() {
    }

    public EmailMensagemDTO(String assunto, String corpo) {
        this.assunto = assunto;
        this.corpo = corpo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }
}
