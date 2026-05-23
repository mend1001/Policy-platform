package com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "roles")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "rol_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "rol_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "rol_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "rol_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rol_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "rol_name", nullable = false, unique = true)
    private String name;

    @Column(name = "rol_description")
    private String description;
}
