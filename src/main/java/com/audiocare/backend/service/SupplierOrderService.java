package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.SupplierOrderRequest;
import com.audiocare.backend.model.Admin;
import com.audiocare.backend.model.InventoryMovement;
import com.audiocare.backend.model.SupplierOrder;
import com.audiocare.backend.model.enums.MovementEventType;
import com.audiocare.backend.repository.AdminRepository;
import com.audiocare.backend.repository.InventoryMovementRepository;
import com.audiocare.backend.repository.SupplierOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierOrderService {

    private final SupplierOrderRepository supplierOrderRepository;
    private final InventoryMovementRepository movementRepository;
    private final AdminRepository adminRepository;

    public List<SupplierOrder> findAll() {
        return supplierOrderRepository.findAllByOrderByReceivedDateDesc();
    }

    public SupplierOrder findById(Integer id) {
        return findOrThrow(id);
    }

    public List<SupplierOrder> findByDateRange(LocalDate from, LocalDate to) {
        return supplierOrderRepository.findByReceivedDateBetweenOrderByReceivedDateDesc(from, to);
    }

    @Transactional
    public SupplierOrder create(SupplierOrderRequest request, Integer adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        SupplierOrder order = SupplierOrder.builder()
                .name(request.getName())
                .receivedDate(request.getReceivedDate())
                .totalAmountEur(request.getTotalAmountEur())
                .totalAmountCrc(request.getTotalAmountCrc())
                .insuranceEur(request.getInsuranceEur() != null ? request.getInsuranceEur() : BigDecimal.ZERO)
                .insuranceCrc(request.getInsuranceCrc() != null ? request.getInsuranceCrc() : BigDecimal.ZERO)
                .createdBy(admin)
                .build();

        SupplierOrder saved = supplierOrderRepository.save(order);

        // Registrar evento en el log de movimientos.
        movementRepository.save(InventoryMovement.builder()
                .eventType(MovementEventType.SUPPLIER_ORDER_CREATED)
                .supplierOrder(saved)
                .admin(admin)
                .description("Pedido de proveedor creado: " + saved.getName())
                .build());

        return saved;
    }

    @Transactional
    public SupplierOrder update(Integer id, SupplierOrderRequest request) {
        SupplierOrder order = findOrThrow(id);

        order.setName(request.getName());
        order.setReceivedDate(request.getReceivedDate());
        order.setTotalAmountEur(request.getTotalAmountEur());
        order.setTotalAmountCrc(request.getTotalAmountCrc());
        order.setInsuranceEur(request.getInsuranceEur() != null ? request.getInsuranceEur() : BigDecimal.ZERO);
        order.setInsuranceCrc(request.getInsuranceCrc() != null ? request.getInsuranceCrc() : BigDecimal.ZERO);

        return supplierOrderRepository.save(order);
    }

    private SupplierOrder findOrThrow(Integer id) {
        return supplierOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido de proveedor no encontrado con id: " + id));
    }
}
