package com.segurosbolivar.polizas.service.impl;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.User;
import com.segurosbolivar.polizas.model.catalog.RiskState;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.repository.RiskRepository;
import com.segurosbolivar.polizas.repository.UserRepository;
import com.segurosbolivar.polizas.repository.catalog.RiskStateRepository;
import com.segurosbolivar.polizas.service.RiskService;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class RiskServiceImpl implements RiskService {

    private static final String MSG_POLIZA_NO_ENCONTRADA = "Póliza no encontrada con id: ";
    private static final String MSG_RIESGO_NO_ENCONTRADO = "Riesgo no encontrado con id: ";
    private static final String STATE_ACTIVO = "ACTIVO";
    private static final String STATE_CANCELADO = "CANCELADO";

    private final PolicyRepository policyRepository;
    private final RiskRepository riskRepository;
    private final RiskStateRepository riskStateRepository;
    private final UserRepository userRepository;
    private final PolicyValidationStrategy agregarRiskValidation;

    public RiskServiceImpl(
            PolicyRepository policyRepository,
            RiskRepository riskRepository,
            RiskStateRepository riskStateRepository,
            UserRepository userRepository,
            @Qualifier("agregarRiskValidation") PolicyValidationStrategy agregarRiskValidation) {
        this.policyRepository = policyRepository;
        this.riskRepository = riskRepository;
        this.riskStateRepository = riskStateRepository;
        this.userRepository = userRepository;
        this.agregarRiskValidation = agregarRiskValidation;
    }

    @Override
    @Transactional
    public RiskResponse agregarRiesgo(UUID polizaId, AgregarRiskRequest request) {
        log.info("Agregando riesgo a póliza id={}, insuredId={}", polizaId, request.getAseguradoId());
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);
        agregarRiskValidation.validate(policy);

        User insured = userRepository.findById(request.getAseguradoId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario asegurado no encontrado con id: " + request.getAseguradoId()));

        RiskState activeState = riskStateRepository.findByName(STATE_ACTIVO)
                .orElseThrow(() -> new BusinessException("Estado ACTIVO no encontrado en catálogo", HttpStatus.INTERNAL_SERVER_ERROR));

        Risk risk = Risk.builder()
                .policy(policy)
                .insured(insured)
                .address(request.getDireccion())
                .state(activeState)
                .build();

        Risk saved = riskRepository.save(risk);
        log.info("Riesgo id={} agregado a póliza id={}", saved.getId(), polizaId);
        return RiskResponse.from(saved);
    }

    @Override
    @Transactional
    public RiskResponse cancelarRiesgo(UUID riesgoId) {
        log.info("Cancelando riesgo id={}", riesgoId);
        Risk risk = riskRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RIESGO_NO_ENCONTRADO + riesgoId));

        RiskState cancelledState = riskStateRepository.findByName(STATE_CANCELADO)
                .orElseThrow(() -> new BusinessException("Estado CANCELADO no encontrado en catálogo", HttpStatus.INTERNAL_SERVER_ERROR));

        risk.setState(cancelledState);
        Risk saved = riskRepository.save(risk);
        log.info("Riesgo id={} cancelado exitosamente", riesgoId);
        return RiskResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RiskResponse> listByPolicy(UUID polizaId, Pageable pageable) {
        buscarPolicyOLanzarExcepcion(polizaId);
        return riskRepository.findByPolicy_Id(polizaId, pageable).map(RiskResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskResponse findById(UUID id) {
        Risk risk = riskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_RIESGO_NO_ENCONTRADO + id));
        return RiskResponse.from(risk);
    }

    private Policy buscarPolicyOLanzarExcepcion(UUID polizaId) {
        return policyRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_POLIZA_NO_ENCONTRADA + polizaId));
    }
}
