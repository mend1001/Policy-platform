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

    @NotNull(message = "Se requiere el valor IPC")
    @DecimalMin(value = "0.0", inclusive = false, message = "IPC debe ser mayor que 0")
    @DecimalMax(value = "1.0", message = "El IPC debe ser un número decimal entre 0 y 1 (por ejemplo, 0,09 para el 9%).")
    @Digits(integer = 1, fraction = 4, message = "El IPC debe tener como máximo 4 decimales")
    private BigDecimal ipc;
}
