package com.segurosbolivar.polizas.service.impl;

import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import com.segurosbolivar.polizas.model.enums.RiskState;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.service.CoreMockService;
import com.segurosbolivar.polizas.service.PolicyService;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PolicyServiceImpl implements PolicyService {

    private static final String EVENTO_ACTUALIZACION = "ACTUALIZACION";
    private static final String MSG_POLIZA_NO_ENCONTRADA = "Póliza no encontrada con id: ";

    private final PolicyRepository policyRepository;
    private final CoreMockService coreMockService;
    private final PolicyValidationStrategy renovarPolicyValidation;

    public PolicyServiceImpl(
            PolicyRepository policyRepository,
            CoreMockService coreMockService,
            @Qualifier("renovarPolicyValidation") PolicyValidationStrategy renovarPolicyValidation) {
        this.policyRepository = policyRepository;
        this.coreMockService = coreMockService;
        this.renovarPolicyValidation = renovarPolicyValidation;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponse> listarPolizas(PolicyType tipo, PolicyState estado) {
        List<Policy> policies = buscarPoliciesConFiltros(tipo, estado);
        return policies.stream().map(PolicyResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskResponse> listarRiesgos(Long polizaId) {
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);
        return policy.getRiesgos().stream().map(RiskResponse::from).toList();
    }

    @Override
    @Transactional
    public PolicyResponse renovarPoliza(Long polizaId, RenovarPolicyRequest request) {
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);
        renovarPolicyValidation.validate(policy);

        BigDecimal canonActualizado = calcularCanonConIpc(policy.getCanon(), request.getIpc());
        BigDecimal primaActualizada = calcularPrima(canonActualizado, policy);

        policy.setCanon(canonActualizado);
        policy.setPrima(primaActualizada);
        policy.setEstado(PolicyState.RENOVADA);

        Policy saved = policyRepository.save(policy);
        notificarCore(polizaId);

        return PolicyResponse.from(saved);
    }

    @Override
    @Transactional
    public PolicyResponse cancelarPoliza(Long polizaId) {
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);

        policy.getRiesgos().stream()
                .filter(risk -> RiskState.ACTIVO.equals(risk.getEstado()))
                .forEach(risk -> risk.setEstado(RiskState.CANCELADO));

        policy.setEstado(PolicyState.CANCELADA);

        Policy saved = policyRepository.save(policy);
        notificarCore(polizaId);

        return PolicyResponse.from(saved);
    }

    private List<Policy> buscarPoliciesConFiltros(PolicyType tipo, PolicyState estado) {
        if (tipo != null && estado != null) return policyRepository.findByTipoAndEstado(tipo, estado);
        if (tipo != null) return policyRepository.findByTipo(tipo);
        if (estado != null) return policyRepository.findByEstado(estado);
        return policyRepository.findAll();
    }

    private Policy buscarPolicyOLanzarExcepcion(Long polizaId) {
        return policyRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_POLIZA_NO_ENCONTRADA + polizaId));
    }

    private BigDecimal calcularCanonConIpc(BigDecimal canon, Double ipc) {
        return canon.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(ipc)));
    }

    private BigDecimal calcularPrima(BigDecimal canon, Policy policy) {
        long meses = ChronoUnit.MONTHS.between(policy.getFechaInicio(), policy.getFechaFin());
        return canon.multiply(BigDecimal.valueOf(meses));
    }

    private void notificarCore(Long polizaId) {
        coreMockService.enviarEvento(CoreEventRequest.builder()
                .evento(EVENTO_ACTUALIZACION)
                .polizaId(polizaId)
                .build());
    }
}
