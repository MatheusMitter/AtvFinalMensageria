package com.atvfinal.mensageria.service;

import com.atvfinal.mensageria.model.Destinatario;
import com.atvfinal.mensageria.repository.DestinatarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinatarioService {

    private final DestinatarioRepository repository;

    public DestinatarioService(DestinatarioRepository repository) {
        this.repository = repository;
    }

    public Destinatario salvar(Destinatario destinatario) {
        if (repository.existsByEmail(destinatario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + destinatario.getEmail());
        }
        return repository.save(destinatario);
    }

    public List<Destinatario> listar() {
        return repository.findAll();
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Destinatário não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    public List<String> listarEmails() {
        return repository.findAll().stream().map(Destinatario::getEmail).toList();
    }
}
