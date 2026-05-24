package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.response.ApiMessages;
import com.segurosbolivar.polizas.dto.response.ApiResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiskController {

    private final RiskService riskService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiskResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(riskService.findById(id), ApiMessages.RISK_FOUND));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<RiskResponse>> cancelarRiesgo(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(riskService.cancelarRiesgo(id), ApiMessages.RISK_CANCELLED));
    }
}
