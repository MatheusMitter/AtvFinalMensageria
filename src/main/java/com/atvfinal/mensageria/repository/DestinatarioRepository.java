package com.atvfinal.mensageria.repository;

import com.atvfinal.mensageria.model.Destinatario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {
    Optional<Destinatario> findByEmail(String email);
    boolean existsByEmail(String email);
}
