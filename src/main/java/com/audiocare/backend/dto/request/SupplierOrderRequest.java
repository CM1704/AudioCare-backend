package com.audiocare.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class SupplierOrderRequest {

    @NotBlank(message = "El nombre del pedido es obligatorio")
    @Size(max = 100)
    private String name;

    @NotNull(message = "La fecha de recepción es obligatoria")
    private LocalDate receivedDate;

    @NotNull(message = "El monto total en euros es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto EUR debe ser mayor a 0")
    private BigDecimal totalAmountEur;

    @NotNull(message = "El monto total en colones es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto CRC debe ser mayor a 0")
    private BigDecimal totalAmountCrc;

    // El seguro es opcional (puede ser 0 si el envío no tiene seguro).
    @DecimalMin(value = "0.0", message = "El seguro EUR no puede ser negativo")
    private BigDecimal insuranceEur;

    @DecimalMin(value = "0.0", message = "El seguro CRC no puede ser negativo")
    private BigDecimal insuranceCrc;
}
