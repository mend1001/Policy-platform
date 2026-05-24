package com.segurosbolivar.polizas.service.validation.impl;

import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component("renewPolicyValidation")
public class RenewPolicyValidation implements PolicyValidationStrategy {

    private static final String MSG_NO_RENOVAR_CANCELADA = "No se puede renovar una póliza cancelada";

    @Override
    public void validate(Policy policy) {
        if ("CANCELADA".equals(policy.getState().getName())) {
            throw new BusinessException(MSG_NO_RENOVAR_CANCELADA, HttpStatus.BAD_REQUEST);
        }
    }
}
