package com.segurosbolivar.polizas.model.catalog;

import com.segurosbolivar.polizas.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "risk_states")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "rst_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "rst_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "rst_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "rst_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskState extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rst_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "rst_name", nullable = false, unique = true)
    private String name;

    @Column(name = "rst_description")
    private String description;
}
