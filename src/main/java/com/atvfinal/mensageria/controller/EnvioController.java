package com.atvfinal.mensageria.controller;

import com.atvfinal.mensageria.dto.EmailMensagemDTO;
import com.atvfinal.mensageria.service.EnvioEmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioEmailService envioEmailService;

    public EnvioController(EnvioEmailService envioEmailService) {
        this.envioEmailService = envioEmailService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> solicitarEnvio(@Valid @RequestBody EmailMensagemDTO dto) {
        int total = envioEmailService.solicitarEnvioParaTodos(dto);
        return ResponseEntity.accepted().body(Map.of(
                "mensagem", "Solicitação publicada na fila com sucesso",
                "totalDestinatarios", total,
                "assunto", dto.getAssunto()
        ));
    }
}
