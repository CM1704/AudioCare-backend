package com.audiocare.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class OrderClientRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Integer clientId;

    @NotBlank(message = "El número de factura es obligatorio")
    @Size(max = 80)
    private String invoiceNum;

    @NotNull(message = "La fecha de venta es obligatoria")
    private LocalDate saleDate;

    @Size(max = 400)
    private String notes;

    // IDs de los productos físicos (serializados) que se incluyen en esta venta.
    // El backend valida que cada producto esté AVAILABLE y no esté en otra orden.
    @NotEmpty(message = "La orden debe contener al menos un producto")
    private List<Integer> productIds;
}