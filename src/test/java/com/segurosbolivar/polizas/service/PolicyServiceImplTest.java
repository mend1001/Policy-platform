package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import com.segurosbolivar.polizas.model.enums.RiskState;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.service.impl.PolicyServiceImpl;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import com.segurosbolivar.polizas.service.validation.impl.RenovarPolicyValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private CoreMockService coreMockService;

    private PolicyValidationStrategy renovarPolicyValidation;
    private PolicyServiceImpl policyService;

    @BeforeEach
    void setUp() {
        renovarPolicyValidation = new RenovarPolicyValidation();
        policyService = new PolicyServiceImpl(policyRepository, coreMockService, renovarPolicyValidation);
    }

    @Test
    void deberiaListarTodasLasPolizasSinFiltros() {
        List<Policy> policies = List.of(polizaActivaIndividual(), polizaActivaColectiva());
        when(policyRepository.findAll()).thenReturn(policies);

        List<PolicyResponse> result = policyService.listarPolizas(null, null);

        assertThat(result).hasSize(2);
        verify(policyRepository).findAll();
    }

    @Test
    void deberiaListarPolizasFiltrandoPorTipo() {
        when(policyRepository.findByTipo(PolicyType.INDIVIDUAL)).thenReturn(List.of(polizaActivaIndividual()));

        List<PolicyResponse> result = policyService.listarPolizas(PolicyType.INDIVIDUAL, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo(PolicyType.INDIVIDUAL);
        verify(policyRepository).findByTipo(PolicyType.INDIVIDUAL);
    }

    @Test
    void deberiaListarPolizasFiltrandoPorEstado() {
        when(policyRepository.findByEstado(PolicyState.ACTIVA)).thenReturn(List.of(polizaActivaIndividual()));

        List<PolicyResponse> result = policyService.listarPolizas(null, PolicyState.ACTIVA);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEstado()).isEqualTo(PolicyState.ACTIVA);
        verify(policyRepository).findByEstado(PolicyState.ACTIVA);
    }

    @Test
    void deberiaListarPolizasFiltrandoPorTipoYEstado() {
        when(policyRepository.findByTipoAndEstado(PolicyType.COLECTIVA, PolicyState.ACTIVA))
                .thenReturn(List.of(polizaActivaColectiva()));

        List<PolicyResponse> result = policyService.listarPolizas(PolicyType.COLECTIVA, PolicyState.ACTIVA);

        assertThat(result).hasSize(1);
        verify(policyRepository).findByTipoAndEstado(PolicyType.COLECTIVA, PolicyState.ACTIVA);
    }

    @Test
    void deberiaListarRiesgosDeLaPoliza() {
        Policy policy = polizaActivaColectivaConRiesgos();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        List<RiskResponse> result = policyService.listarRiesgos(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void deberiaLanzarExcepcionAlListarRiesgosDePolicyInexistente() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.listarRiesgos(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaRenovarPolizaExitosamente() {
        Policy policy = polizaActivaIndividual();
        BigDecimal canonOriginal = policy.getCanon();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        RenovarPolicyRequest request = new RenovarPolicyRequest(0.09);
        PolicyResponse result = policyService.renovarPoliza(1L, request);

        assertThat(result.getEstado()).isEqualTo(PolicyState.RENOVADA);
        assertThat(result.getCanon()).isGreaterThan(canonOriginal);
        verify(coreMockService).enviarEvento(any());
    }

    @Test
    void deberiaLanzarExcepcionSiPolizaCancelada() {
        Policy policy = polizaCancelada();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        RenovarPolicyRequest request = new RenovarPolicyRequest(0.09);

        assertThatThrownBy(() -> policyService.renovarPoliza(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelada");

        verify(policyRepository, never()).save(any());
    }

    @Test
    void deberiaCancelarPolizaYSusRiesgosActivos() {
        Policy policy = polizaActivaColectivaConRiesgos();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(inv -> inv.getArgument(0));

        PolicyResponse result = policyService.cancelarPoliza(1L);

        assertThat(result.getEstado()).isEqualTo(PolicyState.CANCELADA);
        assertThat(policy.getRiesgos()).allMatch(r -> r.getEstado() == RiskState.CANCELADO);
        verify(coreMockService).enviarEvento(any());
    }

    @Test
    void deberiaLanzarExcepcionAlCancelarPolizaInexistente() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.cancelarPoliza(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private Policy polizaActivaIndividual() {
        return Policy.builder()
                .id(1L)
                .tipo(PolicyType.INDIVIDUAL)
                .estado(PolicyState.ACTIVA)
                .canon(new BigDecimal("1500000.00"))
                .prima(new BigDecimal("18000000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2025, 1, 1))
                .tomadorId(1L)
                .beneficiarioId(2L)
                .riesgos(new ArrayList<>())
                .build();
    }

    private Policy polizaActivaColectiva() {
        return Policy.builder()
                .id(2L)
                .tipo(PolicyType.COLECTIVA)
                .estado(PolicyState.ACTIVA)
                .canon(new BigDecimal("3500000.00"))
                .prima(new BigDecimal("84000000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2026, 1, 1))
                .tomadorId(5L)
                .beneficiarioId(6L)
                .riesgos(new ArrayList<>())
                .build();
    }

    private Policy polizaActivaColectivaConRiesgos() {
        Policy policy = polizaActivaColectiva();
        Risk riesgo1 = Risk.builder().id(1L).poliza(policy).aseguradoId(10L)
                .direccion("Calle 72 # 10-34").estado(RiskState.ACTIVO).build();
        Risk riesgo2 = Risk.builder().id(2L).poliza(policy).aseguradoId(11L)
                .direccion("Carrera 15 # 93-45").estado(RiskState.ACTIVO).build();
        policy.getRiesgos().add(riesgo1);
        policy.getRiesgos().add(riesgo2);
        return policy;
    }

    private Policy polizaCancelada() {
        return Policy.builder()
                .id(1L)
                .tipo(PolicyType.INDIVIDUAL)
                .estado(PolicyState.CANCELADA)
                .canon(new BigDecimal("1500000.00"))
                .prima(new BigDecimal("18000000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1))
                .fechaFin(LocalDate.of(2025, 1, 1))
                .tomadorId(1L)
                .beneficiarioId(2L)
                .riesgos(new ArrayList<>())
                .build();
    }
}
