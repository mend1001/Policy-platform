package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.config.AppProperties;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskController.class)
class RiskControllerTest {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE  = "123456";

    private static final UUID RISK_ID    = UUID.fromString("550e8400-e29b-41d4-a716-446655440010");
    private static final UUID POLICY_ID  = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID INSURED_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440020");
    private static final UUID UNKNOWN_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskService riskService;

    @MockitoBean
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        when(appProperties.getApiKey()).thenReturn(API_KEY_VALUE);
    }

    @Test
    void deberiaCancelarRiesgoExitosamente() throws Exception {
        RiskResponse cancelado = RiskResponse.builder()
                .id(RISK_ID).policyId(POLICY_ID).insuredId(INSURED_ID)
                .address("Calle 100 # 9-67, Bogotá").state("CANCELADO").build();

        when(riskService.cancelarRiesgo(RISK_ID)).thenReturn(cancelado);

        mockMvc.perform(post("/riesgos/" + RISK_ID + "/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.state").value("CANCELADO"))
                .andExpect(jsonPath("$.data.id").value(RISK_ID.toString()));
    }

    @Test
    void deberiaRetornar404AlCancelarRiesgoInexistente() throws Exception {
        when(riskService.cancelarRiesgo(UNKNOWN_ID))
                .thenThrow(new ResourceNotFoundException("Riesgo no encontrado con id: " + UNKNOWN_ID));

        mockMvc.perform(post("/riesgos/" + UNKNOWN_ID + "/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deberiaRetornar401SinApiKey() throws Exception {
        mockMvc.perform(post("/riesgos/" + RISK_ID + "/cancelar"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value(401));
    }
}
