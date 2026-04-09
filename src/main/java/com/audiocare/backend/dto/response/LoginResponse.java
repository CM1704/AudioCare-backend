package com.audiocare.backend.dto.response;

import com.audiocare.backend.model.AdminPermissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder @AllArgsConstructor
public class LoginResponse {
    private String token;
    private Integer adminId;
    private String identityNumber;
    private String name;
    private String lastName1;
    private String lastName2;
    private String email;
    private Boolean isMaster;
    // Permisos incluidos en el login para que el front pueda
    // renderizar/ocultar módulos sin llamadas adicionales.
    private AdminPermissions permissions;
}