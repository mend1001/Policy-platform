package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PolicyService {

    Page<PolicyResponse> listarPolizas(String tipo, String estado, Pageable pageable);

    PolicyResponse findById(UUID id);

    Page<PolicyResponse> findByBeneficiary(UUID beneficiaryId, Pageable pageable);

    Page<PolicyResponse> findByHolder(UUID holderId, Pageable pageable);

    PolicyResponse renovarPoliza(UUID polizaId, RenovarPolicyRequest request);

    PolicyResponse cancelarPoliza(UUID polizaId);
}
