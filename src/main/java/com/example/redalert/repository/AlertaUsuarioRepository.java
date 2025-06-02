package com.example.redalert.repository;

import com.example.redalert.model.AlertaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaUsuarioRepository extends JpaRepository<AlertaUsuario, Long>, JpaSpecificationExecutor<AlertaUsuario> {

    // Para o Módulo de Alertas, inicialmente, os métodos herdados do JpaRepository
    // e a capacidade de usar JpaSpecificationExecutor no service layer para
    // construir as queries dinâmicas do GET /alerts são suficientes.
    //
    // Se, durante a implementação do AlertaUsuarioService, você identificar
    // alguma consulta muito específica e repetitiva que não se encaixe bem
    // com Specifications, você pode adicionar um método de query derivado aqui.
    // Por exemplo:
    // Optional<AlertaUsuario> findFirstByOrderByTimestampReporteDesc(); // Para pegar o alerta mais recente
}