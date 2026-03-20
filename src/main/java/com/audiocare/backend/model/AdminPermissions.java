package com.audiocare.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_permissions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AdminPermissions {

    // PK compartida con admin via @MapsId.
    // No se declara campo id separado — @MapsId lo gestiona solo.
    @Id
    @Column(name = "admin_id")
    private Integer adminId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    // Módulo: model_product
    @Column(name = "acc_model_r",    nullable = false) @Builder.Default private Boolean modelRead = false;
    @Column(name = "acc_model_crud", nullable = false) @Builder.Default private Boolean modelCrud = false;

    // Módulo: supplier_order
    @Column(name = "acc_supplier_order_r",   nullable = false) @Builder.Default private Boolean supplierOrderRead = false;
    @Column(name = "acc_supplier_order_cru", nullable = false) @Builder.Default private Boolean supplierOrderCru  = false;

    // Módulo: product
    @Column(name = "acc_product_r",    nullable = false) @Builder.Default private Boolean productRead = false;
    @Column(name = "acc_product_crud", nullable = false) @Builder.Default private Boolean productCrud = false;

    // Módulo: inventory_movement (solo lectura)
    @Column(name = "acc_movements_r", nullable = false) @Builder.Default private Boolean movementsRead = false;

    // Módulo: client
    @Column(name = "acc_client_r",    nullable = false) @Builder.Default private Boolean clientRead = false;
    @Column(name = "acc_client_crud", nullable = false) @Builder.Default private Boolean clientCrud = false;

    // Módulo: order_client (ventas)
    @Column(name = "acc_sale_r",    nullable = false) @Builder.Default private Boolean saleRead = false;
    @Column(name = "acc_sale_crud", nullable = false) @Builder.Default private Boolean saleCrud = false;
}