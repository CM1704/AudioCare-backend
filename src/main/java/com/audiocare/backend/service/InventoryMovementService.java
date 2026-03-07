package com.audiocare.backend.service;

import com.audiocare.backend.model.InventoryMovement;
import com.audiocare.backend.model.enums.MovementEventType;
import com.audiocare.backend.repository.InventoryMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryMovementService {

    private final InventoryMovementRepository movementRepository;

    public List<InventoryMovement> findAll() {
        return movementRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<InventoryMovement> findByEventType(MovementEventType eventType) {
        return movementRepository.findByEventTypeOrderByCreatedAtDesc(eventType);
    }

    public List<InventoryMovement> findByProduct(Integer productId) {
        return movementRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    public List<InventoryMovement> findBySupplierOrder(Integer supplierOrderId) {
        return movementRepository.findBySupplierOrderIdOrderByCreatedAtDesc(supplierOrderId);
    }

    public List<InventoryMovement> findByOrder(Integer orderId) {
        return movementRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public List<InventoryMovement> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return movementRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    }

    public List<InventoryMovement> findByEventTypeAndDateRange(
            MovementEventType eventType, LocalDateTime from, LocalDateTime to) {
        return movementRepository.findByEventTypeAndCreatedAtBetweenOrderByCreatedAtDesc(eventType, from, to);
    }
}