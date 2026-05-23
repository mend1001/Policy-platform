package com.segurosbolivar.polizas.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@AttributeOverrides({
    @AttributeOverride(name = "createdBy", column = @Column(name = "usr_created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "usr_updated_by")),
    @AttributeOverride(name = "createdAt", column = @Column(name = "usr_created_at")),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "usr_updated_at"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usr_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "usr_name", nullable = false)
    private String name;

    @Column(name = "usr_lastname")
    private String lastname;

    @Column(name = "usr_email", nullable = false, unique = true)
    private String email;

    @Column(name = "usr_phone")
    private String phone;

    @Column(name = "usr_doc_type", nullable = false)
    private String docType;

    @Column(name = "usr_doc_number", nullable = false, unique = true)
    private String docNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "usr_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
