package com.audiocare.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminRequest {

    @NotBlank(message = "El número de identidad es obligatorio")
    @Size(max = 30, message = "El número de identidad no puede superar 30 caracteres")
    private String identityNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    private String name;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50)
    private String lastName1;

    @NotBlank(message = "El segundo apellido es obligatorio")
    @Size(max = 50)
    private String lastName2;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Size(max = 100)
    private String email;

    // Solo requerido al crear. En actualización puede venir null (no cambia la contraseña).
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull(message = "El campo isMaster es obligatorio")
    private Boolean isMaster;
}