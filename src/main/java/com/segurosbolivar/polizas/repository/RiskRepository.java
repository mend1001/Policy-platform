package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskRepository extends JpaRepository<Risk, Long> {

    List<Risk> findByPolizaId(Long polizaId);
}
