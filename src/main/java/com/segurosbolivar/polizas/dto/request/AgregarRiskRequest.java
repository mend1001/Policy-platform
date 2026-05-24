package com.segurosbolivar.polizas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarRiskRequest {

    @NotNull(message = "Insured user ID is required")
    private UUID insuredId;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 255, message = "La dirección debe tener entre 10 y 255 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9\\s#\\-.,áéíóúÁÉÍÓÚñÑ]+$",
             message = "Address contains invalid characters")
    private String address;

    @Positive(message = "Insured value must be positive")
    @Digits(integer = 15, fraction = 2, message = "El valor asegurado debe tener como máximo 2 decimales.")
    private BigDecimal insuredValue;
}
