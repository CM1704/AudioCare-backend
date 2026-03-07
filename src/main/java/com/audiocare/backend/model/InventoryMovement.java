package com.audiocare.backend.model;

import com.audiocare.backend.model.enums.MovementEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_movement",
        indexes = {
                @Index(name = "idx_mov_event",   columnList = "event_type"),
                @Index(name = "idx_mov_date",    columnList = "created_at"),
                @Index(name = "idx_mov_product", columnList = "product_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movement")
    private Integer id;

    // Tipo de evento que originó este registro en el log.
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private MovementEventType eventType;

    // Producto físico involucrado (nullable: SUPPLIER_ORDER_CREATED no tiene producto).
    // SET NULL si el producto es eliminado (preserva el historial del log).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Pedido del proveedor relacionado (nullable: solo para SUPPLIER_ORDER_CREATED y PRODUCT_ADDED).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_order_id")
    private SupplierOrder supplierOrder;

    // Orden de cliente relacionada (nullable: solo para PRODUCT_SOLD y PRODUCT_SALE_CANCELED).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderClient order;

    // Admin que realizó la acción. SET NULL si el admin es eliminado.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "description", length = 400)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}