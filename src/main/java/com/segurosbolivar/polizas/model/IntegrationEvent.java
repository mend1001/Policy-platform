package com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "integration_events")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "iev_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "iev_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "iev_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "iev_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationEvent extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "iev_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pol_id", nullable = false)
    private Policy policy;

    @Column(name = "iev_type", nullable = false)
    private String type;

    @Column(name = "iev_state", nullable = false)
    private String state;

    @Column(name = "iev_payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "iev_correlation_id", nullable = false)
    private UUID correlationId;

    @Builder.Default
    @Column(name = "iev_retries", nullable = false)
    private Integer retries = 0;

    @Column(name = "iev_error", columnDefinition = "TEXT")
    private String error;

    @Column(name = "iev_sent_at")
    private LocalDateTime sentAt;
}
