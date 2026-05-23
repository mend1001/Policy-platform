package com.segurosbolivar.polizas.service;

import com.segurosbolivar.polizas.dto.request.RenovarPolicyRequest;
import com.segurosbolivar.polizas.dto.response.PolicyResponse;
import com.segurosbolivar.polizas.dto.response.RiskResponse;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;

import java.util.List;

public interface PolicyService {

    List<PolicyResponse> listarPolizas(PolicyType tipo, PolicyState estado);

    List<RiskResponse> listarRiesgos(Long polizaId);

    PolicyResponse renovarPoliza(Long polizaId, RenovarPolicyRequest request);

    PolicyResponse cancelarPoliza(Long polizaId);
}
