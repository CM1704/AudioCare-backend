package com.audiocare.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "order_detail",
        uniqueConstraints = @UniqueConstraint(name = "ux_detail_product", columnNames = "product_id"),
        indexes           = @Index(name = "idx_detail_order", columnList = "order_id")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_order_detail")
    private Integer id;

    // Orden de venta a la que pertenece este detalle.
    // CASCADE desde OrderClient gestiona la eliminación.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderClient order;

    // Producto físico (unidad serializada) vendido en esta línea.
    // UNIQUE garantiza que una unidad física solo aparezca en UNA venta.
    // RESTRICT: no se puede eliminar un producto ya vendido.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    // El precio de venta NO se almacena aquí.
    // Se obtiene siempre desde product.model.priceSale via JOIN,
    // reflejando el precio actual del modelo en el momento de consulta.
}