package com.audiocare.backend.model;

import com.audiocare.backend.model.enums.ClientType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "client",
        uniqueConstraints = @UniqueConstraint(name = "ux_client_identity", columnNames = "identity_number"),
        indexes          = @Index(name = "idx_client_name", columnList = "name")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Integer id;

    // Cédula física (PRIVATE) o cédula jurídica (DISTRIBUTOR). Siempre requerida.
    @Column(name = "identity_number", length = 30, nullable = false)
    private String identityNumber;

    // Para PRIVATE: primer nombre o nombre completo.
    // Para DISTRIBUTOR: razón social de la empresa.
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "last_name1", length = 50)
    private String lastName1;

    @Column(name = "last_name2", length = 50)
    private String lastName2;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ClientType type;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
