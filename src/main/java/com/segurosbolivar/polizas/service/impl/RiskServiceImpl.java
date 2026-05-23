package com.segurosbolivar.polizas.service.impl;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.enums.RiskState;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.repository.RiskRepository;
import com.segurosbolivar.polizas.service.RiskService;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RiskServiceImpl implements RiskService {

    private static final String MSG_POLIZA_NO_ENCONTRADA = "Póliza no encontrada con id: ";
    private static final String MSG_RIESGO_NO_ENCONTRADO = "Riesgo no encontrado con id: ";

    private final PolicyRepository policyRepository;
    private final RiskRepository riskRepository;
    private final PolicyValidationStrategy agregarRiskValidation;

    public RiskServiceImpl(
            PolicyRepository policyRepository,
            RiskRepository riskRepository,
            @Qualifier("agregarRiskValidation") PolicyValidationStrategy agregarRiskValidation) {
        this.policyRepository = policyRepository;
        this.riskRepository = riskRepository;
        this.agregarRiskValidation = agregarRiskValidation;
    }

    @Override
    @Transactional
    public RiskResponse agregarRiesgo(Long polizaId, AgregarRiskRequest request) {
        log.info("Agregando riesgo a póliza id={}, aseguradoId={}", polizaId, request.getAseguradoId());
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);
        agregarRiskValidation.validate(policy);

        Risk risk = Risk.builder()
                .poliza(policy)
                .aseguradoId(request.getAseguradoId())
                .direccion(request.getDireccion())
                .estado(RiskState.ACTIVO)
                .build();

        Risk saved = riskRepository.save(risk);
        log.info("Riesgo id={} agregado a póliza id={}", saved.getId(), polizaId);
        return RiskResponse.from(saved);
    }

    @Override
    @Transactional
    public RiskResponse cancelarRiesgo(Long riesgoId) {
        log.info("Cancelando riesgo id={}", riesgoId);
        Risk risk = riskRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RIESGO_NO_ENCONTRADO + riesgoId));

        risk.setEstado(RiskState.CANCELADO);
        Risk saved = riskRepository.save(risk);
        log.info("Riesgo id={} cancelado exitosamente", riesgoId);
        return RiskResponse.from(saved);
    }

    private Policy buscarPolicyOLanzarExcepcion(Long polizaId) {
        return policyRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_POLIZA_NO_ENCONTRADA + polizaId));
    }
}
