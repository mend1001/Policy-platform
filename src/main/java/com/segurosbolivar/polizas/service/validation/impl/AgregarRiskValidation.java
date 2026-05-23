package com.segurosbolivar.polizas.service.validation.impl;

import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AgregarRiskValidation implements PolicyValidationStrategy {

    private static final String MSG_SOLO_COLECTIVA = "Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA";

    @Override
    public void validate(Policy policy) {
        if (!PolicyType.COLECTIVA.equals(policy.getTipo())) {
            throw new BusinessException(MSG_SOLO_COLECTIVA, HttpStatus.BAD_REQUEST);
        }
    }
}
