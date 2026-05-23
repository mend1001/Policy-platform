package com.segurosbolivar.polizas.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoreEventRequest {

    private String evento;
    private Long polizaId;
}
