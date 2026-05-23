package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.Renewal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RenewalRepository extends JpaRepository<Renewal, UUID> {
    List<Renewal> findByPolicy_Id(UUID policyId);
}
