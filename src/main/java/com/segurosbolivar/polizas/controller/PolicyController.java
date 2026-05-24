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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final RiskService riskService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PolicyResponse>>> listPolicies(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.listPolicies(tipo, estado, pageable), ApiMessages.POLICIES_LISTED));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolicyResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.findById(id), ApiMessages.POLICY_FOUND));
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    public ResponseEntity<ApiResponse<Page<PolicyResponse>>> findByBeneficiary(
            @PathVariable UUID beneficiaryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.findByBeneficiary(beneficiaryId, pageable), ApiMessages.POLICIES_LISTED));
    }

    @GetMapping("/holder/{holderId}")
    public ResponseEntity<ApiResponse<Page<PolicyResponse>>> findByHolder(
            @PathVariable UUID holderId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.findByHolder(holderId, pageable), ApiMessages.POLICIES_LISTED));
    }

    @GetMapping("/{id}/risks")
    public ResponseEntity<ApiResponse<Page<RiskResponse>>> listRisks(
            @PathVariable UUID id,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.ok(riskService.listByPolicy(id, pageable), ApiMessages.RISKS_LISTED));
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<ApiResponse<PolicyResponse>> renewPolicy(
            @PathVariable UUID id,
            @RequestBody @Valid RenovarPolicyRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.renewPolicy(id, request), ApiMessages.POLICY_RENEWED));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<PolicyResponse>> cancelPolicy(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok(policyService.cancelPolicy(id), ApiMessages.POLICY_CANCELLED));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<ApiResponse<RiskResponse>> addRisk(
            @PathVariable UUID id,
            @RequestBody @Valid AgregarRiskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.created(riskService.addRisk(id, request), ApiMessages.RISK_ADDED));
    }
}
