package com.audiocare.backend.controller;

import com.audiocare.backend.dto.request.SupplierOrderRequest;
import com.audiocare.backend.model.SupplierOrder;
import com.audiocare.backend.security.JwtService;
import com.audiocare.backend.service.SupplierOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/audiocare/api/supplier-orders")
@RequiredArgsConstructor
public class SupplierOrderController {

    private final SupplierOrderService supplierOrderService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<SupplierOrder>> findAll() {
        return ResponseEntity.ok(supplierOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierOrder> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(supplierOrderService.findById(id));
    }

    @GetMapping("/range")
    public ResponseEntity<List<SupplierOrder>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(supplierOrderService.findByDateRange(from, to));
    }

    @PostMapping
    public ResponseEntity<SupplierOrder> create(
            @Valid @RequestBody SupplierOrderRequest request,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierOrderService.create(request, adminId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierOrder> update(
            @PathVariable Integer id,
            @Valid @RequestBody SupplierOrderRequest request) {
        return ResponseEntity.ok(supplierOrderService.update(id, request));
    }

    private Integer extractAdminId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractAdminId(token);
    }
}