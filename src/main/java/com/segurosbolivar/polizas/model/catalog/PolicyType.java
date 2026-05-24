package com.segurosbolivar.polizas.model.catalog;

import com.segurosbolivar.polizas.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "policy_types")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "pty_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "pty_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "pty_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "pty_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyType extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pty_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pty_name", nullable = false, unique = true)
    private String name;

    @Column(name = "pty_description")
    private String description;
}
