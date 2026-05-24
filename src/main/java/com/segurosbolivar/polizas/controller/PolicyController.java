package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.ApiMessages;
import com.segurosbolivar.polizas.dto.response.ApiResponse;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.service.PolicyService;
import com.segurosbolivar.polizas.service.RiskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final RiskService riskService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PolicyResponse>>> listarPolizas(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.listarPolizas(tipo, estado), ApiMessages.POLICIES_LISTED));
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<ApiResponse<List<RiskResponse>>> listarRiesgos(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.listarRiesgos(id), ApiMessages.RISKS_LISTED));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<ApiResponse<PolicyResponse>> renovarPoliza(
            @PathVariable UUID id,
            @RequestBody @Valid RenovarPolicyRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.renovarPoliza(id, request), ApiMessages.POLICY_RENEWED));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<PolicyResponse>> cancelarPoliza(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.cancelarPoliza(id), ApiMessages.POLICY_CANCELLED));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<ApiResponse<RiskResponse>> agregarRiesgo(
            @PathVariable UUID id,
            @RequestBody @Valid AgregarRiskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(riskService.agregarRiesgo(id, request), ApiMessages.RISK_ADDED));
    }
}
