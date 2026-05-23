package com.segurosbolivar.polizas.model;

import com.segurosbolivar.polizas.model.catalog.RiskState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "risks")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "ris_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "ris_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "ris_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "ris_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Risk extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ris_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pol_id", nullable = false)
    private Policy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rst_id", nullable = false)
    private RiskState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id_insured", nullable = false)
    private User insured;

    @Column(name = "ris_address", nullable = false)
    private String address;

    @Column(name = "ris_insured_value", precision = 19, scale = 2)
    private BigDecimal insuredValue;

    @Column(name = "ris_core_id")
    private String coreId;
}
