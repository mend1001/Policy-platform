package com.segurosbolivar.polizas.controller;

import com.segurosbolivar.polizas.dto.request.AgregarRiskRequest;
import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import com.segurosbolivar.polizas.service.PolicyService;
import com.segurosbolivar.polizas.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final RiskService riskService;

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> listarPolizas(
            @RequestParam(required = false) PolicyType tipo,
            @RequestParam(required = false) PolicyState estado) {
        return ResponseEntity.ok(policyService.listarPolizas(tipo, estado));
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<RiskResponse>> listarRiesgos(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.listarRiesgos(id));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<PolicyResponse> renovarPoliza(
            @PathVariable Long id,
            @RequestBody RenovarPolicyRequest request) {
        return ResponseEntity.ok(policyService.renovarPoliza(id, request));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PolicyResponse> cancelarPoliza(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.cancelarPoliza(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiskResponse> agregarRiesgo(
            @PathVariable Long id,
            @RequestBody AgregarRiskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(riskService.agregarRiesgo(id, request));
    }
}
