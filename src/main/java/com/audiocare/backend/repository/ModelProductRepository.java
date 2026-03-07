package com.audiocare.backend.repository;

import com.audiocare.backend.model.ModelProduct;
import com.audiocare.backend.model.enums.ModelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelProductRepository extends JpaRepository<ModelProduct, Integer> {

    // Buscar modelo por código del fabricante (unique).
    Optional<ModelProduct> findByModelCode(Integer modelCode);

    // Validaciones de unicidad antes de crear/actualizar un modelo.
    boolean existsByModelCode(Integer modelCode);
    boolean existsByModelCodeAndIdNot(Integer modelCode, Integer id);

    // Filtrar modelos por estado de stock.
    // Usado en el módulo de inventario para mostrar disponibles o sin stock.
    List<ModelProduct> findByStatus(ModelStatus status);

    // Búsqueda por nombre para el módulo de inventario (filtro por modelo).
    List<ModelProduct> findByNameContainingIgnoreCase(String name);
}