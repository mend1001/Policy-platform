package com.segurosbolivar.polizas.dto.response;

import com.segurosbolivar.polizas.model.Risk;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
public class RiskResponse {

    private UUID id;
    private UUID policyId;
    private UUID insuredId;
    private String address;
    private BigDecimal insuredValue;
    private String state;

    public static RiskResponse from(Risk risk) {
        return RiskResponse.builder()
                .id(risk.getId())
                .policyId(risk.getPolicy().getId())
                .insuredId(risk.getInsured().getId())
                .address(risk.getAddress())
                .insuredValue(risk.getInsuredValue())
                .state(risk.getState().getName())
                .build();
    }
}
