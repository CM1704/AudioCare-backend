package com.audiocare.backend.dto.response;

import com.audiocare.backend.model.AdminPermissions;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
public class AdminResponse {
    private Integer id;
    private String identityNumber;
    private String name;
    private String lastName1;
    private String lastName2;
    private String email;
    private Boolean isMaster;
    private LocalDateTime createdAt;
    // Permisos incluidos en la respuesta. Null si el admin es master
    // (master tiene acceso total, no necesita consultar permisos).
    private AdminPermissions permissions;
}