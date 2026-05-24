package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;

import java.util.List;
import java.util.UUID;

public interface PolicyService {

    List<PolicyResponse> listarPolizas(String tipo, String estado);

    List<RiskResponse> listarRiesgos(UUID polizaId);

    PolicyResponse renovarPoliza(UUID polizaId, RenovarPolicyRequest request);

    PolicyResponse cancelarPoliza(UUID polizaId);
}
