package com.segurosbolivar.polizas.service.validation;

import com.segurosbolivar.polizas.model.Policy;

@FunctionalInterface
public interface PolicyValidationStrategy {

    void validate(Policy policy);
}
