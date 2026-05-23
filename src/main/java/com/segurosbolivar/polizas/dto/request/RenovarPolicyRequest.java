package com.segurosbolivar.polizas.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenovarPolicyRequest {

    @NotNull(message = "El IPC es obligatorio")
    @Positive(message = "El IPC debe ser un valor positivo mayor a cero")
    private Double ipc;
}
