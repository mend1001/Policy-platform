package com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "not_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "not_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "not_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "not_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "not_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pol_id", nullable = false)
    private Policy policy;

    @Column(name = "not_type", nullable = false)
    private String type;

    @Column(name = "not_channel", nullable = false)
    private String channel;

    @Column(name = "not_recipient", nullable = false)
    private String recipient;

    @Column(name = "not_state", nullable = false)
    private String state;

    @Builder.Default
    @Column(name = "not_retries")
    private Integer retries = 0;

    @Column(name = "not_payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "not_sent_at")
    private LocalDateTime sentAt;
}
