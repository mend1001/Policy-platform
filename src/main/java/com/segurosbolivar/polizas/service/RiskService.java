package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;

import java.util.UUID;

public interface RiskService {

    RiskResponse agregarRiesgo(UUID polizaId, AgregarRiskRequest request);

    RiskResponse cancelarRiesgo(UUID riesgoId);
}
