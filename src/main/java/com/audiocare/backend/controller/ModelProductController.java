package com.audiocare.backend.controller;

import com.audiocare.backend.dto.request.ModelProductRequest;
import com.audiocare.backend.model.ModelProduct;
import com.audiocare.backend.model.enums.ModelStatus;
import com.audiocare.backend.service.ModelProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audiocare/api/models")
@RequiredArgsConstructor
public class ModelProductController {

    private final ModelProductService modelProductService;

    @GetMapping
    public ResponseEntity<List<ModelProduct>> findAll() {
        return ResponseEntity.ok(modelProductService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelProduct> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(modelProductService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ModelProduct>> findByStatus(@PathVariable ModelStatus status) {
        return ResponseEntity.ok(modelProductService.findByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ModelProduct>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(modelProductService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<ModelProduct> create(@Valid @RequestBody ModelProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(modelProductService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModelProduct> update(
            @PathVariable Integer id,
            @Valid @RequestBody ModelProductRequest request) {
        return ResponseEntity.ok(modelProductService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        modelProductService.delete(id);
        return ResponseEntity.noContent().build();
    }
}