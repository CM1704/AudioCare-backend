package com.audiocare.backend.repository;

import com.audiocare.backend.model.OrderClient;
import com.audiocare.backend.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderClientRepository extends JpaRepository<OrderClient, Integer> {

    // Buscar orden por número de factura (único, ingresado manualmente).
    Optional<OrderClient> findByInvoiceNum(String invoiceNum);

    // Validación de unicidad del número de factura antes de crear/actualizar.
    boolean existsByInvoiceNum(String invoiceNum);
    boolean existsByInvoiceNumAndIdNot(String invoiceNum, Integer id);

    // ── Consultas para el módulo de ventas ─────────────────────────────────

    // Listar todas las órdenes NO eliminadas (soft delete),
    // ordenadas por fecha de venta descendente (más recientes primero).
    List<OrderClient> findByDeletedFalseOrderBySaleDateDesc();

    // Filtrar órdenes activas por estado.
    List<OrderClient> findByStatusAndDeletedFalseOrderBySaleDateDesc(OrderStatus status);

    // Filtrar órdenes activas por cliente.
    List<OrderClient> findByClientIdAndDeletedFalseOrderBySaleDateDesc(Integer clientId);

    // Filtrar órdenes activas por rango de fechas.
    List<OrderClient> findBySaleDateBetweenAndDeletedFalseOrderBySaleDateDesc(
            LocalDate from, LocalDate to);

    // Historial completo de ventas de un cliente (incluyendo canceladas).
    List<OrderClient> findByClientIdOrderBySaleDateDesc(Integer clientId);
}