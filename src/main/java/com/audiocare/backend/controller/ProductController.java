package com.audiocare.backend.controller;

import com.audiocare.backend.dto.request.ProductRequest;
import com.audiocare.backend.model.Product;
import com.audiocare.backend.model.enums.ProductStatus;
import com.audiocare.backend.security.JwtService;
import com.audiocare.backend.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audiocare/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtService jwtService;

    // Lista todos los productos AVAILABLE ordenados por entry_date ASC (FIFO).
    @GetMapping
    public ResponseEntity<List<Product>> findAvailable() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAllIncludingBilled());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/serial/{serialNum}")
    public ResponseEntity<Product> findBySerial(@PathVariable String serialNum) {
        return productService.findBySerialNum(serialNum)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Productos de un modelo específico filtrados por estado.
    @GetMapping("/model/{modelId}")
    public ResponseEntity<List<Product>> findByModel(
            @PathVariable Integer modelId,
            @RequestParam(defaultValue = "AVAILABLE") ProductStatus status) {
        return ResponseEntity.ok(productService.findByModel(modelId, status));
    }

    // Productos que llegaron en un pedido de proveedor específico.
    @GetMapping("/supplier-order/{supplierOrderId}")
    public ResponseEntity<List<Product>> findBySupplierOrder(
            @PathVariable Integer supplierOrderId) {
        return ResponseEntity.ok(productService.findBySupplierOrder(supplierOrderId));
    }

    // El producto AVAILABLE más antiguo de un modelo (sugerencia FIFO para ventas).
    @GetMapping("/fifo/{modelId}")
    public ResponseEntity<Product> findOldestAvailable(@PathVariable Integer modelId) {
        return productService.findOldestAvailable(modelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(
            @Valid @RequestBody ProductRequest request,
            HttpServletRequest httpRequest) {
        Integer adminId = extractAdminId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request, adminId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Integer extractAdminId(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7);
        return jwtService.extractAdminId(token);
    }
}