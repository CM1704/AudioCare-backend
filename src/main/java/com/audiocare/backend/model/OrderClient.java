package com.audiocare.backend.model;

import com.audiocare.backend.model.enums.OrderStatus;
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
        name = "order_client",
        uniqueConstraints = @UniqueConstraint(name = "ux_order_invoice", columnNames = "invoice_num"),
        indexes = {
                @Index(name = "idx_order_sale_date", columnList = "sale_date"),
                @Index(name = "idx_order_client",    columnList = "client_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_client")
    private Integer id;

    // Cliente al que pertenece esta venta.
    // RESTRICT: no se puede eliminar un cliente con órdenes registradas.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // Número de factura ingresado manualmente por el admin.
    // AudioCare maneja su propio sistema de facturación externo.
    @Column(name = "invoice_num", length = 80, nullable = false)
    private String invoiceNum;

    // Fecha en que se efectuó la venta.
    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    // Monto total calculado en backend sumando price_sale de cada
    // model_product de los productos incluidos en la orden.
    @Column(name = "total_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "notes", length = 400)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Admin createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private Admin updatedBy;

    // Borrado lógico: la orden no se elimina físicamente de la DB.
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Productos físicos incluidos en esta orden.
    // CASCADE ALL: los detalles se crean y eliminan con la orden.
    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderDetail> details = new ArrayList<>();
}