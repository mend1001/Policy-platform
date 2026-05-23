package com.segurosbolivar.polizas.dto.response;

import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
public class PolicyResponse {

    private Long id;
    private PolicyType tipo;
    private PolicyState estado;
    private BigDecimal canon;
    private BigDecimal prima;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long tomadorId;
    private Long beneficiarioId;

    public static PolicyResponse from(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .tipo(policy.getTipo())
                .estado(policy.getEstado())
                .canon(policy.getCanon())
                .prima(policy.getPrima())
                .fechaInicio(policy.getFechaInicio())
                .fechaFin(policy.getFechaFin())
                .tomadorId(policy.getTomadorId())
                .beneficiarioId(policy.getBeneficiarioId())
                .build();
    }
}
