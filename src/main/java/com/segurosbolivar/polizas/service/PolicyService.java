package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PolicyService {

    Page<PolicyResponse> listPolicies(String tipo, String estado, Pageable pageable);

    PolicyResponse findById(UUID id);

    Page<PolicyResponse> findByBeneficiary(UUID beneficiaryId, Pageable pageable);

    Page<PolicyResponse> findByHolder(UUID holderId, Pageable pageable);

    PolicyResponse renewPolicy(UUID polizaId, RenovarPolicyRequest request);

    PolicyResponse cancelPolicy(UUID polizaId);
}
