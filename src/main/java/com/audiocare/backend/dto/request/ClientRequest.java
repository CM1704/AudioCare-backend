package com.audiocare.backend.dto.request;

import com.audiocare.backend.model.enums.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClientRequest {

    @NotBlank(message = "El número de identidad es obligatorio")
    @Size(max = 30)
    private String identityNumber;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String lastName1;

    @Size(max = 50)
    private String lastName2;

    @NotNull(message = "El tipo de cliente es obligatorio")
    private ClientType type;

    @Email(message = "Formato de correo inválido")
    @Size(max = 100)
    private String email;

    @Size(max = 30)
    private String phone;
}