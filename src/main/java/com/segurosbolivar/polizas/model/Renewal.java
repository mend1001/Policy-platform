package com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "renewals")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "ren_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "ren_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "ren_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "ren_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Renewal extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ren_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pol_id", nullable = false)
    private Policy policy;

    @Column(name = "ren_canon_before", precision = 19, scale = 2, nullable = false)
    private BigDecimal canonBefore;

    @Column(name = "ren_canon_after", precision = 19, scale = 2, nullable = false)
    private BigDecimal canonAfter;

    @Column(name = "ren_premium_before", precision = 19, scale = 2, nullable = false)
    private BigDecimal premiumBefore;

    @Column(name = "ren_premium_after", precision = 19, scale = 2, nullable = false)
    private BigDecimal premiumAfter;

    @Column(name = "ren_ipc_applied", precision = 5, scale = 4, nullable = false)
    private BigDecimal ipcApplied;

    @Column(name = "ren_type", nullable = false)
    private String type;

    @Column(name = "ren_result", nullable = false)
    private String result;

    @Column(name = "ren_core_sync_status")
    private String coreSyncStatus;

    @Column(name = "ren_observation", columnDefinition = "TEXT")
    private String observation;
}
