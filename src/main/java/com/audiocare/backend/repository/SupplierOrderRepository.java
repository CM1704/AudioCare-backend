package com.audiocare.backend.repository;

import com.audiocare.backend.model.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Integer> {

    // Búsqueda por nombre descriptivo del pedido.
    List<SupplierOrder> findByNameContainingIgnoreCase(String name);

    // Filtrar pedidos por rango de fechas de recepción.
    // Útil para el módulo de pedidos del proveedor al buscar por año/periodo.
    List<SupplierOrder> findByReceivedDateBetweenOrderByReceivedDateDesc(
            LocalDate from, LocalDate to);

    // Listar todos los pedidos ordenados del más reciente al más antiguo.
    List<SupplierOrder> findAllByOrderByReceivedDateDesc();
}