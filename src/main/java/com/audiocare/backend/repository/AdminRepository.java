package com.audiocare.backend.repository;

import com.audiocare.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    // Autenticación: buscar admin por email para el login.
    Optional<Admin> findByEmail(String email);

    // Validaciones de unicidad antes de crear/actualizar un admin.
    boolean existsByEmail(String email);
    boolean existsByIdentityNumber(String identityNumber);

    // Excluir el propio registro al validar unicidad en edición.
    boolean existsByEmailAndIdNot(String email, Integer id);
    boolean existsByIdentityNumberAndIdNot(String identityNumber, Integer id);
}