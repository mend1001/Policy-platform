package com.segurosbolivar.polizas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenovarPolicyRequest {

    @NotNull(message = "IPC value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "IPC must be greater than 0")
    @DecimalMax(value = "1.0", message = "IPC must be a decimal between 0 and 1 (e.g. 0.09 for 9%)")
    @Digits(integer = 1, fraction = 4, message = "IPC must have at most 4 decimal places")
    private BigDecimal ipc;
}
