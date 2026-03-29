package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.AdminPermissionsRequest;
import com.audiocare.backend.dto.request.AdminRequest;
import com.audiocare.backend.dto.response.AdminResponse;
import com.audiocare.backend.model.Admin;
import com.audiocare.backend.model.AdminPermissions;
import com.audiocare.backend.repository.AdminPermissionsRepository;
import com.audiocare.backend.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminPermissionsRepository permissionsRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Mapeo entidad → DTO response ────────────────────────────────────────

    private AdminResponse toResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .identityNumber(admin.getIdentityNumber())
                .name(admin.getName())
                .lastName1(admin.getLastName1())
                .lastName2(admin.getLastName2())
                .email(admin.getEmail())
                .isMaster(admin.getIsMaster())
                .createdAt(admin.getCreatedAt())
                .permissions(admin.getIsMaster() ? null : admin.getPermissions())
                .build();
    }

    // ── Consultas ────────────────────────────────────────────────────────────

    public List<AdminResponse> findAll() {
        return adminRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AdminResponse findById(Integer id) {
        return toResponse(findAdminOrThrow(id));
    }

    // ── Creación ─────────────────────────────────────────────────────────────

    @Transactional
    public AdminResponse create(AdminRequest request) {
        if (adminRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Ya existe un admin con ese correo");
        if (adminRepository.existsByIdentityNumber(request.getIdentityNumber()))
            throw new IllegalArgumentException("Ya existe un admin con ese número de identidad");
        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new IllegalArgumentException("La contraseña es obligatoria al crear un admin");

        Admin admin = Admin.builder()
                .identityNumber(request.getIdentityNumber())
                .name(request.getName())
                .lastName1(request.getLastName1())
                .lastName2(request.getLastName2())
                .email(request.getEmail())
                .passHash(passwordEncoder.encode(request.getPassword()))
                .isMaster(request.getIsMaster())
                .build();

        Admin saved = adminRepository.save(admin);

        // Todo admin no master recibe una fila de permisos con todos en false por defecto.
        // El master gestiona los permisos luego desde su módulo de administración.
        if (!saved.getIsMaster()) {
            AdminPermissions permissions = AdminPermissions.builder()
                    .admin(saved)
                    .build();
            permissionsRepository.save(permissions);
        }

        return toResponse(adminRepository.findById(saved.getId()).orElseThrow());
    }

    // ── Actualización ────────────────────────────────────────────────────────

    @Transactional
    public AdminResponse update(Integer id, AdminRequest request) {
        Admin admin = findAdminOrThrow(id);

        if (adminRepository.existsByEmailAndIdNot(request.getEmail(), id))
            throw new IllegalArgumentException("Ya existe un admin con ese correo");
        if (adminRepository.existsByIdentityNumberAndIdNot(request.getIdentityNumber(), id))
            throw new IllegalArgumentException("Ya existe un admin con ese número de identidad");

        admin.setIdentityNumber(request.getIdentityNumber());
        admin.setName(request.getName());
        admin.setLastName1(request.getLastName1());
        admin.setLastName2(request.getLastName2());
        admin.setEmail(request.getEmail());
        admin.setIsMaster(request.getIsMaster());

        // Solo actualiza la contraseña si se envía una nueva.
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassHash(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(adminRepository.save(admin));
    }

    // ── Gestión de permisos (solo master puede invocar esto) ─────────────────

    @Transactional
    public AdminResponse updatePermissions(Integer adminId, AdminPermissionsRequest request) {
        Admin admin = findAdminOrThrow(adminId);

        if (admin.getIsMaster())
            throw new IllegalArgumentException("Un admin master no necesita permisos explícitos");

        AdminPermissions permissions = permissionsRepository.findByAdminId(adminId)
                .orElseGet(() -> AdminPermissions.builder().admin(admin).build());

        permissions.setModelRead(request.getModelRead());
        permissions.setModelCrud(request.getModelCrud());
        permissions.setSupplierOrderRead(request.getSupplierOrderRead());
        permissions.setSupplierOrderCru(request.getSupplierOrderCru());
        permissions.setProductRead(request.getProductRead());
        permissions.setProductCrud(request.getProductCrud());
        permissions.setMovementsRead(request.getMovementsRead());
        permissions.setClientRead(request.getClientRead());
        permissions.setClientCrud(request.getClientCrud());
        permissions.setSaleRead(request.getSaleRead());
        permissions.setSaleCrud(request.getSaleCrud());

        permissionsRepository.save(permissions);

        return toResponse(adminRepository.findById(adminId).orElseThrow());
    }

    // ── Eliminación ──────────────────────────────────────────────────────────

    @Transactional
    public void delete(Integer id) {
        Admin admin = findAdminOrThrow(id);
        // La FK ON DELETE CASCADE en admin_permissions elimina los permisos automáticamente.
        adminRepository.delete(admin);
    }

    // ── Utilidad interna ─────────────────────────────────────────────────────

    private Admin findAdminOrThrow(Integer id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado con id: " + id));
    }

    @Transactional
    public void changePassword(Integer id, String currentPassword, String newPassword) {
        Admin admin = findAdminOrThrow(id);

        if (!passwordEncoder.matches(currentPassword, admin.getPassHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        admin.setPassHash(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }
}