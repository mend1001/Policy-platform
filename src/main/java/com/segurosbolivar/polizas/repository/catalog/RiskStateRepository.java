package com.segurosbolivar.polizas.repository.catalog;

import com.segurosbolivar.polizas.model.catalog.RiskState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiskStateRepository extends JpaRepository<RiskState, UUID> {
    Optional<RiskState> findByName(String name);
}
