package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Notification;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Renewal;
import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.User;
import com.segurosbolivar.polizas.model.catalog.PolicyState;
import com.segurosbolivar.polizas.model.catalog.PolicyType;
import com.segurosbolivar.polizas.model.catalog.RiskState;
import com.segurosbolivar.polizas.repository.NotificationRepository;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.repository.RenewalRepository;
import com.segurosbolivar.polizas.repository.catalog.PolicyStateRepository;
import com.segurosbolivar.polizas.repository.catalog.RiskStateRepository;
import com.segurosbolivar.polizas.service.impl.PolicyServiceImpl;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import com.segurosbolivar.polizas.service.validation.impl.RenewPolicyValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyStateRepository policyStateRepository;

    @Mock
    private RiskStateRepository riskStateRepository;

    @Mock
    private RenewalRepository renewalRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CoreMockService coreMockService;

    private PolicyValidationStrategy renovarPolicyValidation;
    private PolicyServiceImpl policyService;

    @BeforeEach
    void setUp() {
        renovarPolicyValidation = new RenewPolicyValidation();
        policyService = new PolicyServiceImpl(
                policyRepository, policyStateRepository, riskStateRepository,
                renewalRepository, notificationRepository, coreMockService, renovarPolicyValidation);
    }

    @Test
    void deberiaListarTodasLasPolizasSinFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Policy> policies = List.of(polizaActivaIndividual(), polizaActivaColectiva());
        when(policyRepository.findAll(pageable)).thenReturn(new PageImpl<>(policies));

        Page<PolicyResponse> result = policyService.listPolicies(null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        verify(policyRepository).findAll(pageable);
    }

    @Test
    void deberiaListarPolizasFiltrandoPorTipo() {
        Pageable pageable = PageRequest.of(0, 10);
        when(policyRepository.findByType_Name("INDIVIDUAL", pageable))
                .thenReturn(new PageImpl<>(List.of(polizaActivaIndividual())));

        Page<PolicyResponse> result = policyService.listPolicies("INDIVIDUAL", null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo("INDIVIDUAL");
        verify(policyRepository).findByType_Name("INDIVIDUAL", pageable);
    }

    @Test
    void deberiaListarPolizasFiltrandoPorEstado() {
        Pageable pageable = PageRequest.of(0, 10);
        when(policyRepository.findByState_Name("ACTIVA", pageable))
                .thenReturn(new PageImpl<>(List.of(polizaActivaIndividual())));

        Page<PolicyResponse> result = policyService.listPolicies(null, "ACTIVA", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getState()).isEqualTo("ACTIVA");
        verify(policyRepository).findByState_Name("ACTIVA", pageable);
    }

    @Test
    void deberiaListarPolizasFiltrandoPorTipoYEstado() {
        Pageable pageable = PageRequest.of(0, 10);
        when(policyRepository.findByType_NameAndState_Name("COLECTIVA", "ACTIVA", pageable))
                .thenReturn(new PageImpl<>(List.of(polizaActivaColectiva())));

        Page<PolicyResponse> result = policyService.listPolicies("COLECTIVA", "ACTIVA", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(policyRepository).findByType_NameAndState_Name("COLECTIVA", "ACTIVA", pageable);
    }

    @Test
    void deberiaEncontrarPolizaPorId() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaActivaIndividual(id);
        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));

        PolicyResponse result = policyService.findById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getType()).isEqualTo("INDIVIDUAL");
    }

    @Test
    void deberiaLanzarExcepcionAlBuscarPolizaInexistente() {
        UUID id = UUID.randomUUID();
        when(policyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaListarPolizasPorBeneficiario() {
        UUID benefId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(policyRepository.findByBeneficiary_Id(benefId, pageable))
                .thenReturn(new PageImpl<>(List.of(polizaActivaIndividual())));

        Page<PolicyResponse> result = policyService.findByBeneficiary(benefId, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(policyRepository).findByBeneficiary_Id(benefId, pageable);
    }

    @Test
    void deberiaListarPolizasPorTomador() {
        UUID holderId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(policyRepository.findByHolder_Id(holderId, pageable))
                .thenReturn(new PageImpl<>(List.of(polizaActivaColectiva())));

        Page<PolicyResponse> result = policyService.findByHolder(holderId, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(policyRepository).findByHolder_Id(holderId, pageable);
    }

    @Test
    void deberiaRenovarPolizaExitosamente() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaActivaIndividual(id);
        BigDecimal canonOriginal = policy.getCanon();

        PolicyState renovadaState = estadoPoliza("RENOVADA");
        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(policyStateRepository.findByName("RENOVADA")).thenReturn(Optional.of(renovadaState));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        RenovarPolicyRequest request = new RenovarPolicyRequest(0.09);
        PolicyResponse result = policyService.renewPolicy(id, request);

        assertThat(result.getState()).isEqualTo("RENOVADA");
        assertThat(result.getCanon()).isGreaterThan(canonOriginal);
        verify(coreMockService).notifyCore(any(Policy.class), anyString());
    }

    @Test
    void deberiaLanzarExcepcionSiPolizaCancelada() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaCancelada(id);
        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));

        RenovarPolicyRequest request = new RenovarPolicyRequest(0.09);

        assertThatThrownBy(() -> policyService.renewPolicy(id, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelada");

        verify(policyRepository, never()).save(any());
    }

    @Test
    void deberiaCancelarPolizaYSusRiesgosActivos() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaActivaColectivaConRiesgos(id);

        PolicyState canceladaState = estadoPoliza("CANCELADA");
        RiskState canceladoRiskState = estadoRiesgo("CANCELADO");

        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(riskStateRepository.findByName("CANCELADO")).thenReturn(Optional.of(canceladoRiskState));
        when(policyStateRepository.findByName("CANCELADA")).thenReturn(Optional.of(canceladaState));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        PolicyResponse result = policyService.cancelPolicy(id);

        assertThat(result.getState()).isEqualTo("CANCELADA");
        assertThat(policy.getRisks()).allMatch(r -> "CANCELADO".equals(r.getState().getName()));
        verify(coreMockService).notifyCore(any(Policy.class), anyString());
    }

    @Test
    void deberiaGuardarRenovationAlRenovar() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaActivaIndividual(id);

        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(policyStateRepository.findByName("RENOVADA")).thenReturn(Optional.of(estadoPoliza("RENOVADA")));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        policyService.renewPolicy(id, new RenovarPolicyRequest(0.05));

        verify(renewalRepository).save(any(Renewal.class));
    }

    @Test
    void deberiaGuardarNotificacionAlRenovar() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaActivaIndividual(id);

        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(policyStateRepository.findByName("RENOVADA")).thenReturn(Optional.of(estadoPoliza("RENOVADA")));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        policyService.renewPolicy(id, new RenovarPolicyRequest(0.05));

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void deberiaGuardarNotificacionAlCancelar() {
        UUID id = UUID.randomUUID();
        Policy policy = polizaActivaColectivaConRiesgos(id);

        when(policyRepository.findById(id)).thenReturn(Optional.of(policy));
        when(riskStateRepository.findByName("CANCELADO")).thenReturn(Optional.of(estadoRiesgo("CANCELADO")));
        when(policyStateRepository.findByName("CANCELADA")).thenReturn(Optional.of(estadoPoliza("CANCELADA")));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        policyService.cancelPolicy(id);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void deberiaLanzarExcepcionAlCancelarPolizaInexistente() {
        UUID id = UUID.randomUUID();
        when(policyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.cancelPolicy(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private PolicyType tipoPolitica(String name) {
        return PolicyType.builder().id(UUID.randomUUID()).name(name).build();
    }

    private PolicyState estadoPoliza(String name) {
        return PolicyState.builder().id(UUID.randomUUID()).name(name).build();
    }

    private RiskState estadoRiesgo(String name) {
        return RiskState.builder().id(UUID.randomUUID()).name(name).build();
    }

    private User usuario() {
        return User.builder().id(UUID.randomUUID()).name("Test").email("test@email.com")
                .docType("CC").docNumber(UUID.randomUUID().toString()).build();
    }

    private Policy polizaActivaIndividual(UUID id) {
        return Policy.builder()
                .id(id)
                .type(tipoPolitica("INDIVIDUAL"))
                .state(estadoPoliza("ACTIVA"))
                .holder(usuario())
                .beneficiary(usuario())
                .canon(new BigDecimal("1500000.00"))
                .premium(new BigDecimal("18000000.00"))
                .months(12)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .risks(new ArrayList<>())
                .build();
    }

    private Policy polizaActivaIndividual() {
        return polizaActivaIndividual(UUID.randomUUID());
    }

    private Policy polizaActivaColectiva() {
        return Policy.builder()
                .id(UUID.randomUUID())
                .type(tipoPolitica("COLECTIVA"))
                .state(estadoPoliza("ACTIVA"))
                .holder(usuario())
                .beneficiary(usuario())
                .canon(new BigDecimal("3500000.00"))
                .premium(new BigDecimal("84000000.00"))
                .months(24)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .risks(new ArrayList<>())
                .build();
    }

    private Policy polizaActivaColectivaConRiesgos(UUID policyId) {
        Policy policy = Policy.builder()
                .id(policyId)
                .type(tipoPolitica("COLECTIVA"))
                .state(estadoPoliza("ACTIVA"))
                .holder(usuario())
                .beneficiary(usuario())
                .canon(new BigDecimal("3500000.00"))
                .premium(new BigDecimal("84000000.00"))
                .months(24)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .risks(new ArrayList<>())
                .build();

        RiskState activoState = estadoRiesgo("ACTIVO");
        Risk r1 = Risk.builder().id(UUID.randomUUID()).policy(policy).insured(usuario())
                .address("Calle 72 # 10-34").state(activoState).build();
        Risk r2 = Risk.builder().id(UUID.randomUUID()).policy(policy).insured(usuario())
                .address("Carrera 15 # 93-45").state(activoState).build();
        policy.getRisks().add(r1);
        policy.getRisks().add(r2);
        return policy;
    }

    private Policy polizaCancelada(UUID id) {
        return Policy.builder()
                .id(id)
                .type(tipoPolitica("INDIVIDUAL"))
                .state(estadoPoliza("CANCELADA"))
                .holder(usuario())
                .beneficiary(usuario())
                .canon(new BigDecimal("1500000.00"))
                .premium(new BigDecimal("18000000.00"))
                .months(12)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .risks(new ArrayList<>())
                .build();
    }
}
