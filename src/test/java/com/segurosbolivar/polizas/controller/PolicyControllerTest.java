package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.config.AppProperties;
import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import com.segurosbolivar.polizas.model.enums.RiskState;
import com.segurosbolivar.polizas.service.PolicyService;
import com.segurosbolivar.polizas.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE  = "123456";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PolicyService policyService;

    @MockitoBean
    private RiskService riskService;

    @MockitoBean
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        when(appProperties.getApiKey()).thenReturn(API_KEY_VALUE);
    }

    @Test
    void deberiaListarTodasLasPolizas() throws Exception {
        when(policyService.listarPolizas(null, null)).thenReturn(List.of(polizaResponse()));

        mockMvc.perform(get("/polizas")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("INDIVIDUAL"))
                .andExpect(jsonPath("$[0].estado").value("ACTIVA"));
    }

    @Test
    void deberiaListarPolizasFiltrandoPorTipoYEstado() throws Exception {
        when(policyService.listarPolizas(PolicyType.COLECTIVA, PolicyState.ACTIVA))
                .thenReturn(List.of(polizaColectivaResponse()));

        mockMvc.perform(get("/polizas")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .param("tipo", "COLECTIVA")
                        .param("estado", "ACTIVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("COLECTIVA"));
    }

    @Test
    void deberiaRetornar401SinApiKey() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deberiaListarRiesgosDePoliza() throws Exception {
        when(policyService.listarRiesgos(1L)).thenReturn(List.of(riskResponse()));

        mockMvc.perform(get("/polizas/1/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    void deberiaRetornar404AlListarRiesgosDePolicyInexistente() throws Exception {
        when(policyService.listarRiesgos(99L))
                .thenThrow(new ResourceNotFoundException("Póliza no encontrada con id: 99"));

        mockMvc.perform(get("/polizas/99/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deberiaRenovarPolizaExitosamente() throws Exception {
        PolicyResponse renovada = PolicyResponse.builder()
                .id(1L).tipo(PolicyType.INDIVIDUAL).estado(PolicyState.RENOVADA)
                .canon(new BigDecimal("1635000.00")).prima(new BigDecimal("19620000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1)).fechaFin(LocalDate.of(2025, 1, 1))
                .tomadorId(1L).beneficiarioId(2L).build();

        when(policyService.renovarPoliza(eq(1L), any(RenovarPolicyRequest.class))).thenReturn(renovada);

        mockMvc.perform(post("/polizas/1/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RenovarPolicyRequest(0.09))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"));
    }

    @Test
    void deberiaRetornar400AlRenovarPolizaCancelada() throws Exception {
        when(policyService.renovarPoliza(eq(1L), any(RenovarPolicyRequest.class)))
                .thenThrow(new BusinessException("No se puede renovar una póliza cancelada", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/polizas/1/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RenovarPolicyRequest(0.09))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No se puede renovar una póliza cancelada"));
    }

    @Test
    void deberiaCancelarPolizaExitosamente() throws Exception {
        PolicyResponse cancelada = PolicyResponse.builder()
                .id(1L).tipo(PolicyType.INDIVIDUAL).estado(PolicyState.CANCELADA)
                .canon(new BigDecimal("1500000.00")).prima(new BigDecimal("18000000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1)).fechaFin(LocalDate.of(2025, 1, 1))
                .tomadorId(1L).beneficiarioId(2L).build();

        when(policyService.cancelarPoliza(1L)).thenReturn(cancelada);

        mockMvc.perform(post("/polizas/1/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void deberiaAgregarRiesgoAPolizaColectiva() throws Exception {
        when(riskService.agregarRiesgo(eq(3L), any(AgregarRiskRequest.class))).thenReturn(riskResponse());

        mockMvc.perform(post("/polizas/3/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AgregarRiskRequest(20L, "Calle 100 # 9-67, Bogotá"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void deberiaRetornar400AlAgregarRiesgoAPolizaIndividual() throws Exception {
        when(riskService.agregarRiesgo(eq(1L), any(AgregarRiskRequest.class)))
                .thenThrow(new BusinessException("Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA",
                        HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/polizas/1/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AgregarRiskRequest(20L, "Calle 100 # 9-67"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    private PolicyResponse polizaResponse() {
        return PolicyResponse.builder()
                .id(1L).tipo(PolicyType.INDIVIDUAL).estado(PolicyState.ACTIVA)
                .canon(new BigDecimal("1500000.00")).prima(new BigDecimal("18000000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1)).fechaFin(LocalDate.of(2025, 1, 1))
                .tomadorId(1L).beneficiarioId(2L).build();
    }

    private PolicyResponse polizaColectivaResponse() {
        return PolicyResponse.builder()
                .id(3L).tipo(PolicyType.COLECTIVA).estado(PolicyState.ACTIVA)
                .canon(new BigDecimal("3500000.00")).prima(new BigDecimal("84000000.00"))
                .fechaInicio(LocalDate.of(2024, 1, 1)).fechaFin(LocalDate.of(2026, 1, 1))
                .tomadorId(5L).beneficiarioId(6L).build();
    }

    private RiskResponse riskResponse() {
        return RiskResponse.builder()
                .id(1L).polizaId(3L).aseguradoId(20L)
                .direccion("Calle 100 # 9-67, Bogotá").estado(RiskState.ACTIVO).build();
    }
}
