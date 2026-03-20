package com.audiocare.backend.model;

import com.audiocare.backend.model.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "product",
        uniqueConstraints = @UniqueConstraint(name = "ux_product_serial", columnNames = "serial_num"),
        indexes = {
                @Index(name = "idx_product_model",  columnList = "model_id"),
                @Index(name = "idx_product_entry",  columnList = "entry_date"),
                @Index(name = "idx_product_status", columnList = "status")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Integer id;

    // Número de serie único del fabricante. varchar porque puede tener
    // ceros a la izquierda o caracteres alfanuméricos según el modelo.
    @Column(name = "serial_num", length = 80, nullable = false)
    private String serialNum;

    // Modelo al que pertenece esta unidad física.
    // RESTRICT: no se puede eliminar un modelo que tiene productos registrados.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelProduct model;

    // Pedido del proveedor en el que llegó esta unidad.
    // RESTRICT: no se puede eliminar un pedido que tiene productos asociados.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_order_id", nullable = false)
    private SupplierOrder supplierOrder;

    // AVAILABLE = en stock y disponible para venta.
    // BILLED    = vendido y facturado (estado final).
    // El trigger en DB actualiza model_product.status al cambiar este campo.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.AVAILABLE;

    // Fecha de ingreso al inventario. Usada para ordenamiento FIFO
    // (vender siempre el producto más antiguo primero).
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    // Fecha de venta. NULL mientras el producto esté disponible.
    // Se establece cuando el producto pasa a estado BILLED.
    @Column(name = "sale_date")
    private LocalDate saleDate;

    // Admin que registró esta unidad. SET NULL si el admin es eliminado.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Admin createdBy;
}