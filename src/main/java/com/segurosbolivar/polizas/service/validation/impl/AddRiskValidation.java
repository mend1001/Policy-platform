package com.segurosbolivar.polizas.service.validation.impl;

import com.segurosbolivar.polizas.exception.BusinessException;
import com.segurosbolivar.polizas.model.Policy;
import com.segurosbolivar.polizas.service.validation.PolicyValidationStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.segurosbolivar.polizas.dto.response.ApiMessages.*;

@Component("addRiskValidation")
public class AddRiskValidation implements PolicyValidationStrategy {



    @Override
    public void validate(Policy policy) {
        if (!"COLECTIVA".equals(policy.getType().getName())) {
            throw new BusinessException(MSG_SOLO_COLECTIVA, HttpStatus.BAD_REQUEST);
        }
        if (!"ACTIVA".equals(policy.getState().getName())) {
            throw new BusinessException(MSG_POLIZA_NO_ACTIVA, HttpStatus.CONFLICT);
        }
    }
}
