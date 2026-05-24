package com.segurosbolivar.polizas.repository.catalog;

import com.segurosbolivar.polizas.model.catalog.PolicyState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PolicyStateRepository extends JpaRepository<PolicyState, UUID> {
    Optional<PolicyState> findByName(String name);
}
