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
    @Size(min = 10, max = 255, message = "Address must be between 10 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s#\\-.,áéíóúÁÉÍÓÚñÑ]+$",
             message = "Address contains invalid characters")
    private String address;

    @Positive(message = "Insured value must be positive")
    @Digits(integer = 15, fraction = 2, message = "Insured value must have at most 2 decimal places")
    private BigDecimal insuredValue;
}
