package com.audiocare.backend.controller;

import com.audiocare.backend.model.InventoryMovement;
import com.audiocare.backend.model.enums.MovementEventType;
import com.audiocare.backend.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// Módulo de solo lectura — no expone POST/PUT/DELETE.
// Los movimientos se generan automáticamente por los services,
// nunca de forma manual desde el frontend.
@RestController
@RequestMapping("/audiocare/api/movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService movementService;

    @GetMapping
    public ResponseEntity<List<InventoryMovement>> findAll() {
        return ResponseEntity.ok(movementService.findAll());
    }

    @GetMapping("/event/{eventType}")
    public ResponseEntity<List<InventoryMovement>> findByEventType(
            @PathVariable MovementEventType eventType) {
        return ResponseEntity.ok(movementService.findByEventType(eventType));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryMovement>> findByProduct(
            @PathVariable Integer productId) {
        return ResponseEntity.ok(movementService.findByProduct(productId));
    }

    @GetMapping("/supplier-order/{supplierOrderId}")
    public ResponseEntity<List<InventoryMovement>> findBySupplierOrder(
            @PathVariable Integer supplierOrderId) {
        return ResponseEntity.ok(movementService.findBySupplierOrder(supplierOrderId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<InventoryMovement>> findByOrder(
            @PathVariable Integer orderId) {
        return ResponseEntity.ok(movementService.findByOrder(orderId));
    }

    @GetMapping("/range")
    public ResponseEntity<List<InventoryMovement>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(movementService.findByDateRange(from, to));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<InventoryMovement>> findByEventTypeAndDateRange(
            @RequestParam MovementEventType eventType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(movementService.findByEventTypeAndDateRange(eventType, from, to));
    }
}