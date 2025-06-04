package com.example.redalert.repository;

import com.example.redalert.model.HotspotsEventos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface HotspotsEventosRepository extends JpaRepository<HotspotsEventos, Long>, JpaSpecificationExecutor<HotspotsEventos> {}
