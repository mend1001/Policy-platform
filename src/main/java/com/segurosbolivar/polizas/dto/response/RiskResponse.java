package com.segurosbolivar.polizas.dto.response;

import com.segurosbolivar.polizas.model.Risk;
import com.segurosbolivar.polizas.model.enums.RiskState;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RiskResponse {

    private Long id;
    private Long polizaId;
    private Long aseguradoId;
    private String direccion;
    private RiskState estado;

    public static RiskResponse from(Risk risk) {
        return RiskResponse.builder()
                .id(risk.getId())
                .polizaId(risk.getPoliza().getId())
                .aseguradoId(risk.getAseguradoId())
                .direccion(risk.getDireccion())
                .estado(risk.getEstado())
                .build();
    }
}
