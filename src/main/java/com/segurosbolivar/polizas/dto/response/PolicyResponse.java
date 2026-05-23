package com.segurosbolivar.polizas.dto.response;

import com.segurosbolivar.polizas.model.Policy;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
public class PolicyResponse {

    private UUID id;
    private String type;
    private String state;
    private BigDecimal canon;
    private BigDecimal premium;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean autoRenewal;
    private Integer months;
    private UUID holderId;
    private UUID beneficiaryId;

    public static PolicyResponse from(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .type(policy.getType().getName())
                .state(policy.getState().getName())
                .canon(policy.getCanon())
                .premium(policy.getPremium())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .autoRenewal(policy.getAutoRenewal())
                .months(policy.getMonths())
                .holderId(policy.getHolder().getId())
                .beneficiaryId(policy.getBeneficiary().getId())
                .build();
    }
}
