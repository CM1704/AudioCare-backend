package com.audiocare.backend.model;

import com.audiocare.backend.model.enums.ModelStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "model_product",
        uniqueConstraints = @UniqueConstraint(name = "ux_model_code", columnNames = "model_code")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ModelProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_model_product")
    private Integer id;

    // Código entero único del fabricante europeo.
    @Column(name = "model_code", nullable = false)
    private Integer modelCode;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // Precio de venta al cliente final (en CRC).
    @Column(name = "price_sale", precision = 14, scale = 2, nullable = false)
    private BigDecimal priceSale;

    // Costo de fabricación en euros (moneda del proveedor europeo).
    @Column(name = "cost_fabric_eu", precision = 14, scale = 4, nullable = false)
    private BigDecimal costFabricEur;

    // Costo de fabricación convertido a colones costarricenses.
    @Column(name = "cost_fabric_crc", precision = 14, scale = 2, nullable = false)
    private BigDecimal costFabricCrc;

    // Calculado automáticamente por trigger en DB.
    // DEFAULT NO_STOCK: un modelo nuevo no tiene stock hasta que se registren productos.
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ModelStatus status = ModelStatus.NO_STOCK;

    // Relación inversa: productos físicos de este modelo.
    // JsonIgnore para evitar serialización circular.
    @JsonIgnore
    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}