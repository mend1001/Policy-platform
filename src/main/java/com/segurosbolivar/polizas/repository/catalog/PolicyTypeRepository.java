package com.segurosbolivar.polizas.repository.catalog;

import com.segurosbolivar.polizas.model.catalog.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PolicyTypeRepository extends JpaRepository<PolicyType, UUID> {
    Optional<PolicyType> findByName(String name);
}
