package com.audiocare.backend.repository;

import com.audiocare.backend.model.Product;
import com.audiocare.backend.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Búsqueda por número de serie único (para registro y verificación de duplicados).
    Optional<Product> findBySerialNum(String serialNum);

    // Validación de unicidad del serial antes de crear un producto.
    boolean existsBySerialNum(String serialNum);

    // ── Consultas para el módulo de inventario ──────────────────────────────

    // Listar productos por estado. Ordenados por entry_date ASC
    // para visualizar el inventario con lógica FIFO (más antiguo primero).
    List<Product> findByStatusOrderByEntryDateAsc(ProductStatus status);

    // Filtrar por modelo y estado (ej. productos AVAILABLE de un modelo específico).
    List<Product> findByModelIdAndStatusOrderByEntryDateAsc(Integer modelId, ProductStatus status);

    // Filtrar por pedido del proveedor (para ver qué productos llegaron en un pedido).
    List<Product> findBySupplierOrderIdOrderByEntryDateAsc(Integer supplierOrderId);

    // Filtrar por pedido del proveedor y estado.
    List<Product> findBySupplierOrderIdAndStatus(Integer supplierOrderId, ProductStatus status);

    // ── Consulta FIFO para el módulo de ventas ──────────────────────────────

    // Obtener el producto AVAILABLE más antiguo de un modelo dado.
    // Esta consulta implementa la lógica FIFO: al registrar una venta,
    // el sistema sugiere automáticamente el audífono que lleva más tiempo en inventario.
    @Query("""
        SELECT p FROM Product p
        WHERE p.model.id = :modelId
          AND p.status   = 'AVAILABLE'
        ORDER BY p.entryDate ASC
        LIMIT 1
    """)
    Optional<Product> findOldestAvailableByModel(@Param("modelId") Integer modelId);

    // ── Conteos para dashboards y validaciones ──────────────────────────────

    // Contar unidades disponibles de un modelo (usado por el trigger también en lógica Java).
    long countByModelIdAndStatus(Integer modelId, ProductStatus status);
}