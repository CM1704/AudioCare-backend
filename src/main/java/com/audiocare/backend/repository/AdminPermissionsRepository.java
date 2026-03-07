package com.audiocare.backend.repository;

import com.audiocare.backend.model.AdminPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPermissionsRepository extends JpaRepository<AdminPermissions, Integer> {

    // Obtener permisos de un admin específico.
    // Se usa en cada request autenticado para verificar acceso por módulo.
    Optional<AdminPermissions> findByAdminId(Integer adminId);
}
