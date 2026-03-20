package com.audiocare.backend.repository;

import com.audiocare.backend.model.InventoryMovement;
import com.audiocare.backend.model.enums.MovementEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {

    // Listar todos los movimientos ordenados del más reciente al más antiguo.
    // Vista principal del módulo de movimientos de inventario.
    List<InventoryMovement> findAllByOrderByCreatedAtDesc();

    // Filtrar log por tipo de evento.
    List<InventoryMovement> findByEventTypeOrderByCreatedAtDesc(MovementEventType eventType);

    // Historial de movimientos de un producto específico.
    // Permite trazabilidad completa de una unidad física (cuándo entró, cuándo se vendió).
    List<InventoryMovement> findByProductIdOrderByCreatedAtDesc(Integer productId);

    // Historial de movimientos generados por un pedido de proveedor.
    List<InventoryMovement> findBySupplierOrderIdOrderByCreatedAtDesc(Integer supplierOrderId);

    // Historial de movimientos generados por una orden de cliente.
    List<InventoryMovement> findByOrderIdOrderByCreatedAtDesc(Integer orderId);

    // Filtrar log por rango de fechas.
    List<InventoryMovement> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime from, LocalDateTime to);

    // Filtrar log por tipo de evento y rango de fechas combinados.
    List<InventoryMovement> findByEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            MovementEventType eventType, LocalDateTime from, LocalDateTime to);
}
