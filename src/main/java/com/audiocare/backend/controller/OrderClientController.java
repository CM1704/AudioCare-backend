package com.audiocare.backend.controller;

import com.audiocare.backend.dto.request.OrderClientRequest;
import com.audiocare.backend.model.OrderClient;
import com.audiocare.backend.model.enums.OrderStatus;
import com.audiocare.backend.security.JwtService;
import com.audiocare.backend.service.OrderClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audiocare/api/orders")
@RequiredArgsConstructor
public class OrderClientController {

    private final OrderClientService orderClientService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<OrderClient>> findAll() {
        return ResponseEntity.ok(orderClientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderClient> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderClientService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderClient>> findByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderClientService.findByStatus(status));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderClient>> findByClient(@PathVariable Integer clientId) {
        return ResponseEntity.ok(orderClientService.findByClient(clientId));
    }

    @PostMapping
    public ResponseEntity<OrderClient> create(
            @Valid @RequestBody OrderClientRequest request,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderClientService.create(request, adminId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderClient> update(
            @PathVariable Integer id,
            @Valid @RequestBody OrderClientRequest request,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        return ResponseEntity.ok(orderClientService.update(id, request, adminId));
    }

    // Cambiar estado de la orden (PENDING → CONFIRMED → COMPLETED, etc.).
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderClient> updateStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        return ResponseEntity.ok(orderClientService.updateStatus(id, status, adminId));
    }

    // Cancelar orden y revertir stock de los productos.
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderClient> cancel(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        return ResponseEntity.ok(orderClientService.cancel(id, adminId));
    }

    // Borrado lógico — solo permitido en órdenes CANCELED.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        orderClientService.delete(id, adminId);
        return ResponseEntity.noContent().build();
    }

    private Integer extractAdminId(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return jwtService.extractAdminId(token);
    }
}