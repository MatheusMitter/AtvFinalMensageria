package com.atvfinal.mensageria.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmailLoteMensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String assunto;
    private String corpo;
    private List<String> destinatarios = new ArrayList<>();

    public EmailLoteMensagem() {
    }

    public EmailLoteMensagem(String assunto, String corpo, List<String> destinatarios) {
        this.assunto = assunto;
        this.corpo = corpo;
        this.destinatarios = destinatarios;
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

    public List<String> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(List<String> destinatarios) {
        this.destinatarios = destinatarios;
    }
}
