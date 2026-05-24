package com.segurosbolivar.polizas.repository;

import com.segurosbolivar.polizas.model.IntegrationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IntegrationEventRepository extends JpaRepository<IntegrationEvent, UUID> {
    List<IntegrationEvent> findByState(String state);
    List<IntegrationEvent> findByPolicy_Id(UUID policyId);
}
