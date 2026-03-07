package com.audiocare.backend.controller;

import com.audiocare.backend.dto.request.AdminPermissionsRequest;
import com.audiocare.backend.dto.request.AdminRequest;
import com.audiocare.backend.dto.response.AdminResponse;
import com.audiocare.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audiocare/api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminResponse>> findAll() {
        return ResponseEntity.ok(adminService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(adminService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AdminResponse> create(@Valid @RequestBody AdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody AdminRequest request) {
        return ResponseEntity.ok(adminService.update(id, request));
    }

    // Endpoint exclusivo para el admin master: gestión de permisos por módulo.
    @PutMapping("/{id}/permissions")
    public ResponseEntity<AdminResponse> updatePermissions(
            @PathVariable Integer id,
            @Valid @RequestBody AdminPermissionsRequest request) {
        return ResponseEntity.ok(adminService.updatePermissions(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        adminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
