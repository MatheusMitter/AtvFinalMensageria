package com.atvfinal.mensageria.consumer;

import com.atvfinal.mensageria.dto.EmailLoteMensagem;
import com.atvfinal.mensageria.model.EnvioLog;
import com.atvfinal.mensageria.repository.EnvioLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumer.class);

    private final JavaMailSender mailSender;
    private final EnvioLogRepository envioLogRepository;

    @Value("${app.mail.from}")
    private String remetente;

    @Value("${app.mail.simulate:true}")
    private boolean simular;

    public EmailConsumer(JavaMailSender mailSender, EnvioLogRepository envioLogRepository) {
        this.mailSender = mailSender;
        this.envioLogRepository = envioLogRepository;
    }

    @RabbitListener(queues = "${app.rabbit.queue}")
    public void receber(EmailLoteMensagem mensagem) {
        log.info("Mensagem recebida da fila. Assunto='{}', destinatários={}",
                mensagem.getAssunto(), mensagem.getDestinatarios().size());

        for (String email : mensagem.getDestinatarios()) {
            try {
                if (simular) {
                    log.info("[SIMULAÇÃO] Enviando e-mail para {} | Assunto: {}", email, mensagem.getAssunto());
                    log.info("[SIMULAÇÃO] Corpo: {}", mensagem.getCorpo());
                    Thread.sleep(150);
                } else {
                    SimpleMailMessage smm = new SimpleMailMessage();
                    smm.setFrom(remetente);
                    smm.setTo(email);
                    smm.setSubject(mensagem.getAssunto());
                    smm.setText(mensagem.getCorpo());
                    mailSender.send(smm);
                    log.info("E-mail enviado para {}", email);
                }
                envioLogRepository.save(new EnvioLog(email, mensagem.getAssunto(), "ENVIADO",
                        simular ? "Envio simulado via console" : "Enviado via SMTP"));
            } catch (Exception ex) {
                log.error("Falha ao enviar e-mail para {}: {}", email, ex.getMessage());
                envioLogRepository.save(new EnvioLog(email, mensagem.getAssunto(), "FALHA", ex.getMessage()));
            }
        }

        log.info("Processamento do lote concluído");
    }
}
