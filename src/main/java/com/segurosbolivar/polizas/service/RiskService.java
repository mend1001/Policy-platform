package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;

public interface RiskService {

    RiskResponse agregarRiesgo(Long polizaId, AgregarRiskRequest request);

    RiskResponse cancelarRiesgo(Long riesgoId);
}
