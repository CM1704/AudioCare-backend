package com.audiocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "admin",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_admin_identity", columnNames = "identity_number"),
                @UniqueConstraint(name = "ux_admin_email",    columnNames = "email")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Integer id;

    @Column(name = "identity_number", length = 30, nullable = false)
    private String identityNumber;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "last_name1", length = 50, nullable = false)
    private String lastName1;

    @Column(name = "last_name2", length = 50, nullable = false)
    private String lastName2;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "pass_hash", length = 255, nullable = false)
    private String passHash;

    @Column(name = "is_master", nullable = false)
    @Builder.Default
    private Boolean isMaster = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relación 1:1 con permisos — solo aplica a admins no master.
    // cascade ALL + orphanRemoval garantiza que al eliminar el admin
    // sus permisos se eliminen también (respaldado por ON DELETE CASCADE en DB).
    @OneToOne(mappedBy = "admin", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private AdminPermissions permissions;
}