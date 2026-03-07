package com.audiocare.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ProductRequest {

    @NotBlank(message = "El número de serie es obligatorio")
    @Size(max = 80)
    private String serialNum;

    @NotNull(message = "El modelo es obligatorio")
    private Integer modelId;

    @NotNull(message = "El pedido del proveedor es obligatorio")
    private Integer supplierOrderId;

    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate entryDate;
}