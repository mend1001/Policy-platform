package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiskRepository extends JpaRepository<Risk, UUID> {

    List<Risk> findByPolicy_Id(UUID policyId);
}
