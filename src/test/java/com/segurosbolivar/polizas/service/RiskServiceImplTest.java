package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import com.segurosbolivar.polizas.model.enums.RiskState;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.repository.RiskRepository;
import com.segurosbolivar.polizas.service.impl.RiskServiceImpl;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import com.segurosbolivar.polizas.service.validation.impl.AgregarRiskValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private RiskRepository riskRepository;

    private PolicyValidationStrategy agregarRiskValidation;
    private RiskServiceImpl riskService;

    @BeforeEach
    void setUp() {
        agregarRiskValidation = new AgregarRiskValidation();
        riskService = new RiskServiceImpl(policyRepository, riskRepository, agregarRiskValidation);
    }

    @Test
    void deberiaAgregarRiesgoAPolizaColectivaExitosamente() {
        Policy policy = polizaActivaColectiva();
        when(policyRepository.findById(3L)).thenReturn(Optional.of(policy));
        Risk riesgoGuardado = riesgoActivo(policy);
        when(riskRepository.save(any(Risk.class))).thenReturn(riesgoGuardado);

        AgregarRiskRequest request = new AgregarRiskRequest(20L, "Calle 100 # 9-67, Bogotá");
        RiskResponse result = riskService.agregarRiesgo(3L, request);

        assertThat(result.getEstado()).isEqualTo(RiskState.ACTIVO);
        assertThat(result.getAseguradoId()).isEqualTo(20L);
        verify(riskRepository).save(any(Risk.class));
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarRiesgoAPolizaIndividual() {
        Policy policy = polizaActivaIndividual();
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        AgregarRiskRequest request = new AgregarRiskRequest(20L, "Calle 100 # 9-67, Bogotá");

        assertThatThrownBy(() -> riskService.agregarRiesgo(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("COLECTIVA");

        verify(riskRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarRiesgoAPolicyInexistente() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        AgregarRiskRequest request = new AgregarRiskRequest(20L, "Calle 100");

        assertThatThrownBy(() -> riskService.agregarRiesgo(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaCancelarRiesgoExitosamente() {
        Policy policy = polizaActivaColectiva();
        Risk risk = riesgoActivo(policy);
        when(riskRepository.findById(1L)).thenReturn(Optional.of(risk));
        when(riskRepository.save(any(Risk.class))).thenAnswer(inv -> inv.getArgument(0));

        RiskResponse result = riskService.cancelarRiesgo(1L);

        assertThat(result.getEstado()).isEqualTo(RiskState.CANCELADO);
        verify(riskRepository).save(risk);
    }

    @Test
    void deberiaLanzarExcepcionAlCancelarRiesgoInexistente() {
        when(riskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riskService.cancelarRiesgo(99L))
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
                .id(3L)
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

    private Risk riesgoActivo(Policy policy) {
        return Risk.builder()
                .id(1L)
                .poliza(policy)
                .aseguradoId(20L)
                .direccion("Calle 100 # 9-67, Bogotá")
                .estado(RiskState.ACTIVO)
                .build();
    }
}
