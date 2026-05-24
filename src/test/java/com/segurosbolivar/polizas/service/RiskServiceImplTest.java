package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.User;
import com.segurosbolivar.polizas.model.catalog.PolicyState;
import com.segurosbolivar.polizas.model.catalog.PolicyType;
import com.segurosbolivar.polizas.model.catalog.RiskState;
import com.segurosbolivar.polizas.repository.PolicyRepository;
import com.segurosbolivar.polizas.repository.RiskRepository;
import com.segurosbolivar.polizas.repository.UserRepository;
import com.segurosbolivar.polizas.repository.catalog.RiskStateRepository;
import com.segurosbolivar.polizas.service.impl.RiskServiceImpl;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import com.segurosbolivar.polizas.service.validation.impl.AddRiskValidation;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private RiskRepository riskRepository;

    @Mock
    private RiskStateRepository riskStateRepository;

    @Mock
    private UserRepository userRepository;

    private PolicyValidationStrategy agregarRiskValidation;
    private RiskServiceImpl riskService;

    @BeforeEach
    void setUp() {
        agregarRiskValidation = new AddRiskValidation();
        riskService = new RiskServiceImpl(
                policyRepository, riskRepository, riskStateRepository,
                userRepository, agregarRiskValidation);
    }

    @Test
    void deberiaAgregarRiesgoAPolizaColectivaExitosamente() {
        UUID polizaId = UUID.randomUUID();
        UUID insuredId = UUID.randomUUID();
        Policy policy = polizaActivaColectiva(polizaId);
        User insured = usuario(insuredId);
        RiskState activoState = estadoRiesgo("ACTIVO");

        when(policyRepository.findById(polizaId)).thenReturn(Optional.of(policy));
        when(userRepository.findById(insuredId)).thenReturn(Optional.of(insured));
        when(riskStateRepository.findByName("ACTIVO")).thenReturn(Optional.of(activoState));

        Risk riesgoGuardado = riesgoActivo(policy, insured, activoState);
        when(riskRepository.save(any(Risk.class))).thenReturn(riesgoGuardado);

        AgregarRiskRequest request = AgregarRiskRequest.builder()
                .insuredId(insuredId).address("Calle 100 # 9-67, Bogotá").build();
        RiskResponse result = riskService.addRisk(polizaId, request);

        assertThat(result.getState()).isEqualTo("ACTIVO");
        assertThat(result.getInsuredId()).isEqualTo(insuredId);
        verify(riskRepository).save(any(Risk.class));
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarRiesgoAPolizaIndividual() {
        UUID polizaId = UUID.randomUUID();
        UUID insuredId = UUID.randomUUID();
        Policy policy = polizaActivaIndividual(polizaId);
        when(policyRepository.findById(polizaId)).thenReturn(Optional.of(policy));

        AgregarRiskRequest request = AgregarRiskRequest.builder()
                .insuredId(insuredId).address("Calle 100 # 9-67, Bogotá").build();

        assertThatThrownBy(() -> riskService.addRisk(polizaId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("COLECTIVA");

        verify(riskRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcionAlAgregarRiesgoAPolicyInexistente() {
        UUID polizaId = UUID.randomUUID();
        UUID insuredId = UUID.randomUUID();
        when(policyRepository.findById(polizaId)).thenReturn(Optional.empty());

        AgregarRiskRequest request = AgregarRiskRequest.builder()
                .insuredId(insuredId).address("Calle 100 # 9-67, Bogotá").build();

        assertThatThrownBy(() -> riskService.addRisk(polizaId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaCancelarRiesgoExitosamente() {
        UUID riesgoId = UUID.randomUUID();
        UUID polizaId = UUID.randomUUID();
        UUID insuredId = UUID.randomUUID();

        Policy policy = polizaActivaColectiva(polizaId);
        User insured = usuario(insuredId);
        RiskState activoState = estadoRiesgo("ACTIVO");
        RiskState canceladoState = estadoRiesgo("CANCELADO");

        Risk risk = riesgoActivo(policy, insured, activoState);
        when(riskRepository.findById(riesgoId)).thenReturn(Optional.of(risk));
        when(riskStateRepository.findByName("CANCELADO")).thenReturn(Optional.of(canceladoState));
        when(riskRepository.save(any(Risk.class))).thenAnswer(inv -> inv.getArgument(0));

        RiskResponse result = riskService.cancelRisk(riesgoId);

        assertThat(result.getState()).isEqualTo("CANCELADO");
        verify(riskRepository).save(risk);
    }

    @Test
    void deberiaLanzarExcepcionAlCancelarRiesgoInexistente() {
        UUID riesgoId = UUID.randomUUID();
        when(riskRepository.findById(riesgoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riskService.cancelRisk(riesgoId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaListarRiesgosPorPoliza() {
        UUID polizaId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Policy policy = polizaActivaColectiva(polizaId);
        User insured = usuario(UUID.randomUUID());
        RiskState activo = estadoRiesgo("ACTIVO");
        Risk r1 = riesgoActivo(policy, insured, activo);
        Risk r2 = riesgoActivo(policy, insured, activo);

        when(policyRepository.findById(polizaId)).thenReturn(Optional.of(policy));
        when(riskRepository.findByPolicy_Id(polizaId, pageable))
                .thenReturn(new PageImpl<>(List.of(r1, r2)));

        Page<RiskResponse> result = riskService.listByPolicy(polizaId, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getState()).isEqualTo("ACTIVO");
        verify(riskRepository).findByPolicy_Id(polizaId, pageable);
    }

    @Test
    void deberiaEncontrarRiesgoPorId() {
        UUID riesgoId = UUID.randomUUID();
        UUID polizaId = UUID.randomUUID();
        Policy policy = polizaActivaColectiva(polizaId);
        User insured = usuario(UUID.randomUUID());
        RiskState activo = estadoRiesgo("ACTIVO");
        Risk risk = riesgoActivo(policy, insured, activo);

        when(riskRepository.findById(riesgoId)).thenReturn(Optional.of(risk));

        RiskResponse result = riskService.findById(riesgoId);

        assertThat(result.getState()).isEqualTo("ACTIVO");
        verify(riskRepository).findById(riesgoId);
    }

    @Test
    void deberiaLanzarExcepcionAlBuscarRiesgoInexistente() {
        UUID riesgoId = UUID.randomUUID();
        when(riskRepository.findById(riesgoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riskService.findById(riesgoId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deberiaLanzarExcepcionAlListarRiesgosDePolicyInexistente() {
        UUID polizaId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(policyRepository.findById(polizaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> riskService.listByPolicy(polizaId, pageable))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User usuario(UUID id) {
        return User.builder().id(id).name("Test").email(id + "@email.com")
                .docType("CC").docNumber(id.toString()).build();
    }

    private RiskState estadoRiesgo(String name) {
        return RiskState.builder().id(UUID.randomUUID()).name(name).build();
    }

    private Policy polizaActivaIndividual(UUID id) {
        return Policy.builder()
                .id(id)
                .type(PolicyType.builder().id(UUID.randomUUID()).name("INDIVIDUAL").build())
                .state(PolicyState.builder().id(UUID.randomUUID()).name("ACTIVA").build())
                .holder(usuario(UUID.randomUUID()))
                .beneficiary(usuario(UUID.randomUUID()))
                .canon(new BigDecimal("1500000.00"))
                .premium(new BigDecimal("18000000.00"))
                .months(12)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .risks(new ArrayList<>())
                .build();
    }

    private Policy polizaActivaColectiva(UUID id) {
        return Policy.builder()
                .id(id)
                .type(PolicyType.builder().id(UUID.randomUUID()).name("COLECTIVA").build())
                .state(PolicyState.builder().id(UUID.randomUUID()).name("ACTIVA").build())
                .holder(usuario(UUID.randomUUID()))
                .beneficiary(usuario(UUID.randomUUID()))
                .canon(new BigDecimal("3500000.00"))
                .premium(new BigDecimal("84000000.00"))
                .months(24)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .risks(new ArrayList<>())
                .build();
    }

    private Risk riesgoActivo(Policy policy, User insured, RiskState state) {
        return Risk.builder()
                .id(UUID.randomUUID())
                .policy(policy)
                .insured(insured)
                .address("Calle 100 # 9-67, Bogotá")
                .state(state)
                .build();
    }
}
