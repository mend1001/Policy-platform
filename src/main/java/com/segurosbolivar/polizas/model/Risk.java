package com.segurosbolivar.polizas.model;

import com.segurosbolivar.polizas.model.enums.RiskState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "risks")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Policy poliza;

    @Column(name = "asegurado_id", nullable = false)
    private Long aseguradoId;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private RiskState estado;
}
