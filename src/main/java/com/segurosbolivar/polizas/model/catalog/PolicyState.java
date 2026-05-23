package com.segurosbolivar.polizas.model.catalog;

import com.segurosbolivar.polizas.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "policy_states")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "pst_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "pst_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "pst_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "pst_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyState extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pst_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "pst_name", nullable = false, unique = true)
    private String name;

    @Column(name = "pst_description")
    private String description;
}
