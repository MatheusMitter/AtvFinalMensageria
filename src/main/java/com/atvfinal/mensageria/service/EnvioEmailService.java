package com.atvfinal.mensageria.service;

import com.atvfinal.mensageria.dto.EmailLoteMensagem;
import com.atvfinal.mensageria.dto.EmailMensagemDTO;
import com.atvfinal.mensageria.producer.EmailProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnvioEmailService {

    private static final Logger log = LoggerFactory.getLogger(EnvioEmailService.class);

    private final DestinatarioService destinatarioService;
    private final EmailProducer producer;

    public EnvioEmailService(DestinatarioService destinatarioService, EmailProducer producer) {
        this.destinatarioService = destinatarioService;
        this.producer = producer;
    }

    public int solicitarEnvioParaTodos(EmailMensagemDTO dto) {
        List<String> emails = destinatarioService.listarEmails();
        if (emails.isEmpty()) {
            throw new IllegalStateException("Nenhum destinatário cadastrado para envio");
        }
        EmailLoteMensagem lote = new EmailLoteMensagem(dto.getAssunto(), dto.getCorpo(), emails);
        log.info("Solicitação de envio recebida. Assunto='{}', total destinatários={}", dto.getAssunto(), emails.size());
        producer.publicar(lote);
        return emails.size();
    }
}
