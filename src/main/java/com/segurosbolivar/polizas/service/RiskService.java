package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RiskService {

    RiskResponse agregarRiesgo(UUID polizaId, AgregarRiskRequest request);

    RiskResponse cancelarRiesgo(UUID riesgoId);

    Page<RiskResponse> listByPolicy(UUID polizaId, Pageable pageable);

    RiskResponse findById(UUID id);
}
