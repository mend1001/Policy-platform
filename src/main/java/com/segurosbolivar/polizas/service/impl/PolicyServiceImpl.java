package com.segurosbolivar.polizas.service.impl;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Notification;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Renewal;
import com.segurosbolivar.polizas.model.catalog.PolicyState;
import com.segurosbolivar.polizas.model.catalog.RiskState;
import com.segurosbolivar.polizas.repository.NotificationRepository;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.repository.RenewalRepository;
import com.segurosbolivar.polizas.repository.catalog.PolicyStateRepository;
import com.segurosbolivar.polizas.repository.catalog.RiskStateRepository;
import com.segurosbolivar.polizas.service.CoreMockService;
import com.segurosbolivar.polizas.service.PolicyService;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class PolicyServiceImpl implements PolicyService {

    private static final String MSG_POLIZA_NO_ENCONTRADA = "Póliza no encontrada con id: ";
    private static final String STATE_RENOVADA = "RENOVADA";
    private static final String STATE_CANCELADA = "CANCELADA";
    private static final String STATE_ACTIVE_RISK = "ACTIVO";
    private static final String STATE_CANCELLED_RISK = "CANCELADO";

    private final PolicyRepository policyRepository;
    private final PolicyStateRepository policyStateRepository;
    private final RiskStateRepository riskStateRepository;
    private final RenewalRepository renewalRepository;
    private final NotificationRepository notificationRepository;
    private final CoreMockService coreMockService;
    private final PolicyValidationStrategy renovarPolicyValidation;

    public PolicyServiceImpl(
            PolicyRepository policyRepository,
            PolicyStateRepository policyStateRepository,
            RiskStateRepository riskStateRepository,
            RenewalRepository renewalRepository,
            NotificationRepository notificationRepository,
            CoreMockService coreMockService,
            @Qualifier("renovarPolicyValidation") PolicyValidationStrategy renovarPolicyValidation) {
        this.policyRepository = policyRepository;
        this.policyStateRepository = policyStateRepository;
        this.riskStateRepository = riskStateRepository;
        this.renewalRepository = renewalRepository;
        this.notificationRepository = notificationRepository;
        this.coreMockService = coreMockService;
        this.renovarPolicyValidation = renovarPolicyValidation;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyResponse> listarPolizas(String tipo, String estado, Pageable pageable) {
        Page<Policy> policies;
        if (tipo != null && estado != null) {
            policies = policyRepository.findByType_NameAndState_Name(tipo, estado, pageable);
        } else if (tipo != null) {
            policies = policyRepository.findByType_Name(tipo, pageable);
        } else if (estado != null) {
            policies = policyRepository.findByState_Name(estado, pageable);
        } else {
            policies = policyRepository.findAll(pageable);
        }
        return policies.map(PolicyResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponse findById(UUID id) {
        Policy policy = buscarPolicyOLanzarExcepcion(id);
        return PolicyResponse.from(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyResponse> findByBeneficiary(UUID beneficiaryId, Pageable pageable) {
        return policyRepository.findByBeneficiary_Id(beneficiaryId, pageable).map(PolicyResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyResponse> findByHolder(UUID holderId, Pageable pageable) {
        return policyRepository.findByHolder_Id(holderId, pageable).map(PolicyResponse::from);
    }

    @Override
    @Transactional
    public PolicyResponse renovarPoliza(UUID polizaId, RenovarPolicyRequest request) {
        log.info("Renovando póliza id={}, ipc={}", polizaId, request.getIpc());
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);
        renovarPolicyValidation.validate(policy);

        BigDecimal canonBefore = policy.getCanon();
        BigDecimal premiumBefore = policy.getPremium();

        BigDecimal canonAfter = calcularCanonConIpc(canonBefore, request.getIpc());
        BigDecimal premiumAfter = calcularPrima(canonAfter, policy.getMonths());

        policy.setCanon(canonAfter);
        policy.setPremium(premiumAfter);
        policy.setState(obtenerEstadoOLanzarExcepcion(STATE_RENOVADA));

        Policy saved = policyRepository.save(policy);

        Renewal renewal = Renewal.builder()
                .policy(saved)
                .canonBefore(canonBefore)
                .canonAfter(canonAfter)
                .premiumBefore(premiumBefore)
                .premiumAfter(premiumAfter)
                .ipcApplied(BigDecimal.valueOf(request.getIpc()))
                .type("MANUAL")
                .result("SUCCESS")
                .coreSyncStatus("PENDING")
                .build();
        renewalRepository.save(renewal);

        coreMockService.notifyCore(saved, "POLICY_RENEWED");
        notificationRepository.save(crearNotificacion(saved, "POLICY_RENEWED"));

        log.info("Póliza id={} renovada exitosamente. Nuevo canon={}", polizaId, canonAfter);
        return PolicyResponse.from(saved);
    }

    @Override
    @Transactional
    public PolicyResponse cancelarPoliza(UUID polizaId) {
        log.info("Cancelando póliza id={}", polizaId);
        Policy policy = buscarPolicyOLanzarExcepcion(polizaId);

        RiskState cancelledRiskState = obtenerEstadoRiesgoOLanzarExcepcion(STATE_CANCELLED_RISK);

        long riesgosCancelados = policy.getRisks().stream()
                .filter(risk -> STATE_ACTIVE_RISK.equals(risk.getState().getName()))
                .peek(risk -> risk.setState(cancelledRiskState))
                .count();

        policy.setState(obtenerEstadoOLanzarExcepcion(STATE_CANCELADA));

        Policy saved = policyRepository.save(policy);
        coreMockService.notifyCore(saved, "POLICY_CANCELLED");
        notificationRepository.save(crearNotificacion(saved, "POLICY_CANCELLED"));

        log.info("Póliza id={} cancelada con {} riesgos cancelados", polizaId, riesgosCancelados);
        return PolicyResponse.from(saved);
    }

    private Policy buscarPolicyOLanzarExcepcion(UUID polizaId) {
        return policyRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_POLIZA_NO_ENCONTRADA + polizaId));
    }

    private PolicyState obtenerEstadoOLanzarExcepcion(String name) {
        return policyStateRepository.findByName(name)
                .orElseThrow(() -> new BusinessException("Estado de póliza no encontrado: " + name, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private RiskState obtenerEstadoRiesgoOLanzarExcepcion(String name) {
        return riskStateRepository.findByName(name)
                .orElseThrow(() -> new BusinessException("Estado de riesgo no encontrado: " + name, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private BigDecimal calcularCanonConIpc(BigDecimal canon, Double ipc) {
        return canon.multiply(BigDecimal.ONE.add(BigDecimal.valueOf(ipc)));
    }

    private BigDecimal calcularPrima(BigDecimal canon, Integer months) {
        return canon.multiply(BigDecimal.valueOf(months));
    }

    private Notification crearNotificacion(Policy policy, String type) {
        return Notification.builder()
                .policy(policy)
                .type(type)
                .channel("EMAIL")
                .recipient(policy.getHolder().getEmail())
                .state("PENDING")
                .build();
    }
}
