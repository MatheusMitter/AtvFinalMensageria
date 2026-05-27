package com.atvfinal.mensageria.controller;

import com.atvfinal.mensageria.model.Destinatario;
import com.atvfinal.mensageria.service.DestinatarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/destinatarios")
public class DestinatarioController {

    private final DestinatarioService service;

    public DestinatarioController(DestinatarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Destinatario> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<Destinatario> cadastrar(@Valid @RequestBody Destinatario destinatario) {
        Destinatario salvo = service.salvar(destinatario);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
