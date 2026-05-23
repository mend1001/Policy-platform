package com.segurosbolivar.polizas.model;

import com.segurosbolivar.polizas.model.enums.PolicyState;
import com.segurosbolivar.polizas.model.enums.PolicyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private PolicyType tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private PolicyState estado;

    @Column(name = "canon", nullable = false, precision = 15, scale = 2)
    private BigDecimal canon;

    @Column(name = "prima", nullable = false, precision = 15, scale = 2)
    private BigDecimal prima;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "tomador_id", nullable = false)
    private Long tomadorId;

    @Column(name = "beneficiario_id", nullable = false)
    private Long beneficiarioId;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Risk> riesgos = new ArrayList<>();
}
