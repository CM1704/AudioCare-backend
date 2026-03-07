package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.ModelProductRequest;
import com.audiocare.backend.model.ModelProduct;
import com.audiocare.backend.model.enums.ModelStatus;
import com.audiocare.backend.repository.ModelProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelProductService {

    private final ModelProductRepository modelProductRepository;

    public List<ModelProduct> findAll() {
        return modelProductRepository.findAll();
    }

    public ModelProduct findById(Integer id) {
        return findOrThrow(id);
    }

    public List<ModelProduct> findByStatus(ModelStatus status) {
        return modelProductRepository.findByStatus(status);
    }

    public List<ModelProduct> findByName(String name) {
        return modelProductRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public ModelProduct create(ModelProductRequest request) {
        if (modelProductRepository.existsByModelCode(request.getModelCode()))
            throw new IllegalArgumentException("Ya existe un modelo con ese código: " + request.getModelCode());

        ModelProduct model = ModelProduct.builder()
                .modelCode(request.getModelCode())
                .name(request.getName())
                .priceSale(request.getPriceSale())
                .costFabricEur(request.getCostFabricEur())
                .costFabricCrc(request.getCostFabricCrc())
                // Status inicia en NO_STOCK. El trigger de DB lo actualiza
                // automáticamente cuando se registre el primer producto AVAILABLE.
                .status(ModelStatus.NO_STOCK)
                .build();

        return modelProductRepository.save(model);
    }

    @Transactional
    public ModelProduct update(Integer id, ModelProductRequest request) {
        ModelProduct model = findOrThrow(id);

        if (modelProductRepository.existsByModelCodeAndIdNot(request.getModelCode(), id))
            throw new IllegalArgumentException("Ya existe un modelo con ese código: " + request.getModelCode());

        model.setModelCode(request.getModelCode());
        model.setName(request.getName());
        model.setPriceSale(request.getPriceSale());
        model.setCostFabricEur(request.getCostFabricEur());
        model.setCostFabricCrc(request.getCostFabricCrc());
        // El status NO se toca aquí — es territorio exclusivo del trigger de DB.

        return modelProductRepository.save(model);
    }

    @Transactional
    public void delete(Integer id) {
        // RESTRICT en FK: fallará si hay productos asociados a este modelo.
        modelProductRepository.delete(findOrThrow(id));
    }

    private ModelProduct findOrThrow(Integer id) {
        return modelProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Modelo no encontrado con id: " + id));
    }
}