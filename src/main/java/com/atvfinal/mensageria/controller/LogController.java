package com.atvfinal.mensageria.controller;

import com.atvfinal.mensageria.model.EnvioLog;
import com.atvfinal.mensageria.repository.EnvioLogRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final EnvioLogRepository repository;

    public LogController(EnvioLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EnvioLog> listar() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "processadoEm"));
    }
}
