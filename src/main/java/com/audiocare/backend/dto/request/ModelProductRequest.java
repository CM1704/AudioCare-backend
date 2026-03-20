package com.audiocare.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ModelProductRequest {

    @NotNull(message = "El código de modelo es obligatorio")
    private Integer modelCode;

    @NotBlank(message = "El nombre del modelo es obligatorio")
    @Size(max = 100)
    private String name;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0")
    private BigDecimal priceSale;

    @NotNull(message = "El costo de fabricación en euros es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo EUR debe ser mayor a 0")
    private BigDecimal costFabricEur;

    @NotNull(message = "El costo de fabricación en colones es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo CRC debe ser mayor a 0")
    private BigDecimal costFabricCrc;
}