package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Risk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiskRepository extends JpaRepository<Risk, UUID> {

    Page<Risk> findByPolicy_Id(UUID policyId, Pageable pageable);
}
