package com.audiocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "supplier_order",
        indexes = @Index(name = "idx_supporder_date", columnList = "received_date")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SupplierOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_supplier_order")
    private Integer id;

    // Nombre descriptivo del pedido (ej. "Pedido Europeo Q1 2026").
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // Fecha en que el pedido fue físicamente recibido en AudioCare.
    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    // Monto total del pedido en euros (moneda del proveedor).
    @Column(name = "total_amount_eu", precision = 14, scale = 4, nullable = false)
    private BigDecimal totalAmountEur;

    // Monto total del pedido convertido a colones costarricenses.
    @Column(name = "total_amount_crc", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalAmountCrc;

    // Costo del seguro del envío en euros.
    @Column(name = "insurance_eu", precision = 12, scale = 4, nullable = false)
    @Builder.Default
    private BigDecimal insuranceEur = BigDecimal.ZERO;

    // Costo del seguro del envío en colones.
    @Column(name = "insurance_crc", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal insuranceCrc = BigDecimal.ZERO;

    // Admin que registró el pedido. SET NULL si el admin es eliminado.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Admin createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Productos físicos que llegaron en este pedido.
    @JsonIgnore
    @OneToMany(mappedBy = "supplierOrder", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}