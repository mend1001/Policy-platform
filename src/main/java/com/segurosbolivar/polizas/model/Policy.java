package com.segurosbolivar.polizas.model;

import com.segurosbolivar.polizas.model.catalog.PolicyState;
import com.segurosbolivar.polizas.model.catalog.PolicyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "policies")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "pol_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "pol_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "pol_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "pol_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pol_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pty_id", nullable = false)
    private PolicyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pst_id", nullable = false)
    private PolicyState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id_holder", nullable = false)
    private User holder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id_beneficiary", nullable = false)
    private User beneficiary;

    @Column(name = "pol_canon", precision = 19, scale = 2, nullable = false)
    private BigDecimal canon;

    @Column(name = "pol_premium", precision = 19, scale = 2, nullable = false)
    private BigDecimal premium;

    @Column(name = "pol_months", nullable = false)
    private Integer months;

    @Column(name = "pol_start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "pol_end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "pol_auto_renewal")
    @Builder.Default
    private Boolean autoRenewal = false;

    @Column(name = "pol_core_id")
    private String coreId;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Risk> risks = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Renewal> renewals = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<IntegrationEvent> integrationEvents = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}
