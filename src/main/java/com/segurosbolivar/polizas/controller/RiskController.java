package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiskController {

    private final RiskService riskService;

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<RiskResponse> cancelarRiesgo(@PathVariable UUID id) {
        return ResponseEntity.ok(riskService.cancelarRiesgo(id));
    }
}
