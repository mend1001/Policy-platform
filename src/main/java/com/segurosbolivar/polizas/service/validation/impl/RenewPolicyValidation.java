package com.segurosbolivar.polizas.service.validation.impl;

import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.segurosbolivar.polizas.dto.response.ApiMessages.POLICY_CANCELLED_ERROR;

@Component("renewPolicyValidation")
public class RenewPolicyValidation implements PolicyValidationStrategy {



    @Override
    public void validate(Policy policy) {
        if ("CANCELADA".equals(policy.getState().getName())) {
            throw new BusinessException(POLICY_CANCELLED_ERROR, HttpStatus.BAD_REQUEST);
        }
    }
}
