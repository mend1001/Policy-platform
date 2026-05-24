package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.config.AppProperties;
import com.segurosbolivar.polizas.dto.response.ApiMessages;
import com.segurosbolivar.polizas.dto.request.CoreEventRequest;
import com.segurosbolivar.polizas.service.CoreMockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoreMockController.class)
class CoreMockControllerTest {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE  = "123456";
    private static final UUID POLICY_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    @Value("${api.base-path}")
    private String apiBasePath;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CoreMockService coreMockService;

    @MockitoBean
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        when(appProperties.getApiKey()).thenReturn(API_KEY_VALUE);
    }

    @Test
    void deberiaRegistrarEventoEnCore() throws Exception {
        doNothing().when(coreMockService).sendEvent(any(CoreEventRequest.class));

        CoreEventRequest request = CoreEventRequest.builder()
                .event("ACTUALIZACION")
                .policyId(POLICY_UUID)
                .build();

        mockMvc.perform(post(apiBasePath + "/core-mock/evento")
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.message").value(ApiMessages.CORE_NOTIFIED))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deberiaRetornar401SinApiKey() throws Exception {
        CoreEventRequest request = CoreEventRequest.builder()
                .event("ACTUALIZACION")
                .policyId(POLICY_UUID)
                .build();

        mockMvc.perform(post(apiBasePath + "/core-mock/evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatus").value(401));
    }
}
