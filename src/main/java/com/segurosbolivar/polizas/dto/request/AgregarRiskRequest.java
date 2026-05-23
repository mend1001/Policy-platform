package com.segurosbolivar.polizas.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgregarRiskRequest {

    private Long aseguradoId;
    private String direccion;
}
