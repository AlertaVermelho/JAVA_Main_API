package com.example.redalert.repository;

import com.example.redalert.model.AlertaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaUsuarioRepository extends JpaRepository<AlertaUsuario, Long>, JpaSpecificationExecutor<AlertaUsuario> {

    List<AlertaUsuario> findByStatusAlertaAndHotspotAssociadoIsNull(String statusAlerta);
}
