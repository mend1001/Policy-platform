package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.config.AppProperties;
import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.response.ApiMessages;
import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.service.PolicyService;
import com.segurosbolivar.polizas.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE  = "123456";

    @Value("${api.base-path}")
    private String apiBasePath;

    private static final UUID POLICY_ID    = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID POLICY_COL   = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    private static final UUID HOLDER_ID    = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    private static final UUID BENEFICIARY  = UUID.fromString("550e8400-e29b-41d4-a716-446655440011");
    private static final UUID INSURED_ID   = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");
    private static final UUID RISK_ID      = UUID.fromString("550e8400-e29b-41d4-a716-446655440030");
    private static final UUID UNKNOWN_ID   = UUID.fromString("00000000-0000-0000-0000-000000000099");

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
        when(policyService.listPolicies(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(polizaResponse())));

        mockMvc.perform(get(apiBasePath + "/polizas")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.content[0].type").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.data.content[0].state").value("ACTIVA"))
                .andExpect(jsonPath("$.data.totalElements").exists());
    }

    @Test
    void deberiaResponderConEstructuraPaginadaCompleta() throws Exception {
        when(policyService.listPolicies(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(polizaResponse(), polizaColectivaResponse())));

        mockMvc.perform(get(apiBasePath + "/polizas")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").exists())
                .andExpect(jsonPath("$.data.totalPages").exists())
                .andExpect(jsonPath("$.data.size").exists());
    }

    @Test
    void deberiaListarPolizasFiltrandoPorTipoYEstado() throws Exception {
        when(policyService.listPolicies(eq("COLECTIVA"), eq("ACTIVA"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(polizaColectivaResponse())));

        mockMvc.perform(get(apiBasePath + "/polizas")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .param("tipo", "COLECTIVA")
                        .param("estado", "ACTIVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].type").value("COLECTIVA"));
    }

    @Test
    void deberiaRetornar401SinApiKey() throws Exception {
        mockMvc.perform(get(apiBasePath + "/polizas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value(401))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaObtenerPolizaPorId() throws Exception {
        when(policyService.findById(POLICY_ID)).thenReturn(polizaResponse());

        mockMvc.perform(get(apiBasePath + "/polizas/" + POLICY_ID)
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.type").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.data.state").value("ACTIVA"));
    }

    @Test
    void deberiaRetornar404AlBuscarPolizaInexistente() throws Exception {
        when(policyService.findById(UNKNOWN_ID))
                .thenThrow(new ResourceNotFoundException("Póliza no encontrada con id: " + UNKNOWN_ID));

        mockMvc.perform(get(apiBasePath + "/polizas/" + UNKNOWN_ID)
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaListarPolizasPorBeneficiario() throws Exception {
        when(policyService.findByBeneficiary(eq(BENEFICIARY), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(polizaResponse())));

        mockMvc.perform(get(apiBasePath + "/polizas/beneficiary/" + BENEFICIARY)
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.content[0].type").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.data.totalElements").exists());
    }

    @Test
    void deberiaListarPolizasPorTomador() throws Exception {
        when(policyService.findByHolder(eq(HOLDER_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(polizaResponse())));

        mockMvc.perform(get(apiBasePath + "/polizas/holder/" + HOLDER_ID)
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.content[0].type").value("INDIVIDUAL"))
                .andExpect(jsonPath("$.data.totalElements").exists());
    }

    @Test
    void deberiaListarRiesgosDePoliza() throws Exception {
        when(riskService.listByPolicy(eq(POLICY_ID), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(riskResponse())));

        mockMvc.perform(get(apiBasePath + "/polizas/" + POLICY_ID + "/risks")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.content[0].state").value("ACTIVO"));
    }

    @Test
    void deberiaRetornar404AlListarRiesgosDePolicyInexistente() throws Exception {
        when(riskService.listByPolicy(eq(UNKNOWN_ID), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Póliza no encontrada con id: " + UNKNOWN_ID));

        mockMvc.perform(get(apiBasePath + "/polizas/" + UNKNOWN_ID + "/risks")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaRenovarPolizaExitosamente() throws Exception {
        PolicyResponse renovada = PolicyResponse.builder()
                .id(POLICY_ID).type("INDIVIDUAL").state("RENOVADA")
                .canon(new BigDecimal("1635000.00")).premium(new BigDecimal("19620000.00"))
                .startDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2025, 1, 1))
                .months(12).holderId(HOLDER_ID).beneficiaryId(BENEFICIARY).build();

        when(policyService.renewPolicy(eq(POLICY_ID), any(RenovarPolicyRequest.class))).thenReturn(renovada);

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RenovarPolicyRequest(new BigDecimal("0.09")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.state").value("RENOVADA"));
    }

    @Test
    void deberiaRetornar400AlRenovarPolizaCancelada() throws Exception {
        when(policyService.renewPolicy(eq(POLICY_ID), any(RenovarPolicyRequest.class)))
                .thenThrow(new BusinessException(ApiMessages.POLICY_CANCELLED_ERROR, HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RenovarPolicyRequest(new BigDecimal("0.09")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.message").value(ApiMessages.POLICY_CANCELLED_ERROR));
    }

    @Test
    void deberiaCancelarPolizaExitosamente() throws Exception {
        PolicyResponse cancelada = PolicyResponse.builder()
                .id(POLICY_ID).type("INDIVIDUAL").state("CANCELADA")
                .canon(new BigDecimal("1500000.00")).premium(new BigDecimal("18000000.00"))
                .startDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2025, 1, 1))
                .months(12).holderId(HOLDER_ID).beneficiaryId(BENEFICIARY).build();

        when(policyService.cancelPolicy(POLICY_ID)).thenReturn(cancelada);

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.state").value("CANCELADA"));
    }

    @Test
    void deberiaRetornar404AlCancelarPolizaInexistente() throws Exception {
        when(policyService.cancelPolicy(UNKNOWN_ID))
                .thenThrow(new ResourceNotFoundException("Póliza no encontrada con id: " + UNKNOWN_ID));

        mockMvc.perform(post(apiBasePath + "/polizas/" + UNKNOWN_ID + "/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaRetornar400AlRenovarConIpcNulo() throws Exception {
        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ipc\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaAgregarRiesgoAPolizaColectiva() throws Exception {
        when(riskService.addRisk(eq(POLICY_COL), any(AgregarRiskRequest.class))).thenReturn(riskResponse());

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_COL + "/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AgregarRiskRequest.builder().insuredId(INSURED_ID).address("Calle 100 # 9-67, Bogotá").build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.data.state").value("ACTIVO"));
    }

    @Test
    void deberiaRetornar400AlAgregarRiesgoAPolizaIndividual() throws Exception {
        when(riskService.addRisk(eq(POLICY_ID), any(AgregarRiskRequest.class)))
                .thenThrow(new BusinessException("Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA",
                        HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AgregarRiskRequest.builder().insuredId(INSURED_ID).address("Calle 100 # 9-67, Bogotá").build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaRetornar400ConIpcMayorA1() throws Exception {
        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ipc\":2.5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
    }

    @Test
    void deberiaRetornar400ConIpcCero() throws Exception {
        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ipc\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
    }

    @Test
    void deberiaRetornar400ConBodyAusente() throws Exception {
        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/renovar")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
    }

    @Test
    void deberiaRetornar400ConUUIDInvalido() throws Exception {
        mockMvc.perform(get(apiBasePath + "/polizas/not-a-uuid")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
    }

    @Test
    void deberiaRetornar401SinApiKeyNoRetornar500() throws Exception {
        mockMvc.perform(get(apiBasePath + "/polizas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value(401));
    }

    @Test
    void deberiaRetornar409AlCancelarPolizaYaCancelada() throws Exception {
        when(policyService.cancelPolicy(POLICY_ID))
                .thenThrow(new BusinessException(ApiMessages.MSG_POLIZA_YA_CANCELADA, HttpStatus.CONFLICT));

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.httpStatus").value(409));
    }

    @Test
    void deberiaRetornar409AlAgregarRiesgoAPolizaCancelada() throws Exception {
        when(riskService.addRisk(eq(POLICY_ID), any(AgregarRiskRequest.class)))
                .thenThrow(new BusinessException(ApiMessages.MSG_POLIZA_NO_ACTIVA, HttpStatus.CONFLICT));

        mockMvc.perform(post(apiBasePath + "/polizas/" + POLICY_ID + "/riesgos")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AgregarRiskRequest.builder().insuredId(INSURED_ID).address("Calle 100 # 9-67, Bogotá").build())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.httpStatus").value(409));
    }

    @Test
    void shouldReturn404WhenRouteDoesNotExist() throws Exception {
        mockMvc.perform(get(apiBasePath + "/polizas1")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void shouldReturn404WhenSubRouteDoesNotExist() throws Exception {
        mockMvc.perform(get(apiBasePath + "/polizas/{id}/inexistente", UUID.randomUUID())
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404));
    }

    @Test
    void shouldReturn400WhenPathVariableIsNotUUID() throws Exception {
        mockMvc.perform(get(apiBasePath + "/polizas/no-es-uuid")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Valor no válido")));
    }

    @Test
    void shouldReturn405WhenMethodNotAllowed() throws Exception {
        mockMvc.perform(delete(apiBasePath + "/polizas")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.httpStatus").value(405));
    }

    @Test
    void shouldReturn400WhenBodyIsMissingOnRenovar() throws Exception {
        mockMvc.perform(post(apiBasePath + "/polizas/{id}/renovar", UUID.randomUUID())
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value(400));
    }

    private PolicyResponse polizaResponse() {
        return PolicyResponse.builder()
                .id(POLICY_ID).type("INDIVIDUAL").state("ACTIVA")
                .canon(new BigDecimal("1500000.00")).premium(new BigDecimal("18000000.00"))
                .startDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2025, 1, 1))
                .months(12).holderId(HOLDER_ID).beneficiaryId(BENEFICIARY).build();
    }

    private PolicyResponse polizaColectivaResponse() {
        return PolicyResponse.builder()
                .id(POLICY_COL).type("COLECTIVA").state("ACTIVA")
                .canon(new BigDecimal("3500000.00")).premium(new BigDecimal("84000000.00"))
                .startDate(LocalDate.of(2024, 1, 1)).endDate(LocalDate.of(2026, 1, 1))
                .months(24).holderId(HOLDER_ID).beneficiaryId(BENEFICIARY).build();
    }

    private RiskResponse riskResponse() {
        return RiskResponse.builder()
                .id(RISK_ID).policyId(POLICY_COL).insuredId(INSURED_ID)
                .address("Calle 100 # 9-67, Bogotá").state("ACTIVO").build();
    }
}
