package com.segurosbolivar.polizas.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreEventRequest {

    @NotBlank(message = "El tipo de evento es obligatorio")
    @Size(max = 50, message = "El tipo de evento no debe exceder los 50 caracteres.")
    @Pattern(regexp = "^[A-Z_]+$", message = "\n" +
            "El tipo de evento debe constar únicamente de letras mayúsculas y guiones bajos.")
    private String event;

    @NotNull(message = "Se requiere el ID de la política.")
    private UUID policyId;
}
