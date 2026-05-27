package com.atvfinal.mensageria.producer;

import com.atvfinal.mensageria.dto.EmailLoteMensagem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailProducer {

    private static final Logger log = LoggerFactory.getLogger(EmailProducer.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbit.exchange}")
    private String exchange;

    @Value("${app.rabbit.routing-key}")
    private String routingKey;

    public EmailProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(EmailLoteMensagem mensagem) {
        log.info("Publicando mensagem na exchange [{}] com routing key [{}] para {} destinatários",
                exchange, routingKey, mensagem.getDestinatarios().size());
        rabbitTemplate.convertAndSend(exchange, routingKey, mensagem);
        log.info("Mensagem publicada com sucesso");
    }
}
