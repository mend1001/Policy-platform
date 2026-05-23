package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.config.AppProperties;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.exception.ResourceNotFoundException;
import com.segurosbolivar.polizas.model.enums.RiskState;
import com.segurosbolivar.polizas.service.RiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskController.class)
class RiskControllerTest {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE  = "123456";

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
                .id(1L).polizaId(3L).aseguradoId(20L)
                .direccion("Calle 100 # 9-67, Bogotá").estado(RiskState.CANCELADO).build();

        when(riskService.cancelarRiesgo(1L)).thenReturn(cancelado);

        mockMvc.perform(post("/riesgos/1/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRetornar404AlCancelarRiesgoInexistente() throws Exception {
        when(riskService.cancelarRiesgo(99L))
                .thenThrow(new ResourceNotFoundException("Riesgo no encontrado con id: 99"));

        mockMvc.perform(post("/riesgos/99/cancelar")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deberiaRetornar401SinApiKey() throws Exception {
        mockMvc.perform(post("/riesgos/1/cancelar"))
                .andExpect(status().isUnauthorized());
    }
}
