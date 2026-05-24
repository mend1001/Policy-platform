package com.segurosbolivar.polizas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarRiskRequest {

    @NotNull(message = "El ID del asegurado es obligatorio")
    private UUID aseguradoId;

    @NotBlank(message = "La dirección del inmueble es obligatoria")
    private String direccion;
}
