package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.OrderClientRequest;
import com.audiocare.backend.model.*;
import com.audiocare.backend.model.enums.MovementEventType;
import com.audiocare.backend.model.enums.OrderStatus;
import com.audiocare.backend.model.enums.ProductStatus;
import com.audiocare.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderClientService {

    private final OrderClientRepository orderClientRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private final InventoryMovementRepository movementRepository;

    public List<OrderClient> findAll() {
        return orderClientRepository.findByDeletedFalseOrderBySaleDateDesc();
    }

    public OrderClient findById(Integer id) {
        return findOrThrow(id);
    }

    public List<OrderClient> findByStatus(OrderStatus status) {
        return orderClientRepository.findByStatusAndDeletedFalseOrderBySaleDateDesc(status);
    }

    public List<OrderClient> findByClient(Integer clientId) {
        return orderClientRepository.findByClientIdAndDeletedFalseOrderBySaleDateDesc(clientId);
    }

    // ── Crear orden ──────────────────────────────────────────────────────────

    @Transactional
    public OrderClient create(OrderClientRequest request, Integer adminId) {
        if (orderClientRepository.existsByInvoiceNum(request.getInvoiceNum()))
            throw new IllegalArgumentException("Ya existe una orden con el número de factura: " + request.getInvoiceNum());

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + request.getClientId()));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        // Validar y recolectar productos antes de crear la orden.
        List<Product> products = resolveAndValidateProducts(request.getProductIds());

        // Calcular total sumando el precio actual de cada modelo.
        BigDecimal total = products.stream()
                .map(p -> p.getModel().getPriceSale())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderClient order = OrderClient.builder()
                .client(client)
                .invoiceNum(request.getInvoiceNum())
                .saleDate(request.getSaleDate())
                .totalAmount(total)
                .notes(request.getNotes())
                .createdBy(admin)
                .build();

        OrderClient saved = orderClientRepository.save(order);

        // Crear detalles y marcar cada producto como BILLED.
        List<OrderDetail> details = new ArrayList<>();
        for (Product product : products) {
            details.add(OrderDetail.builder()
                    .order(saved)
                    .product(product)
                    .build());

            product.setStatus(ProductStatus.BILLED);
            product.setSaleDate(request.getSaleDate().atStartOfDay().toLocalDate());
            productRepository.save(product);
            // El trigger recalcula model_product.status automáticamente.

            // Log de movimiento por cada producto vendido.
            movementRepository.save(InventoryMovement.builder()
                    .eventType(MovementEventType.PRODUCT_SOLD)
                    .product(product)
                    .order(saved)
                    .admin(admin)
                    .description("Producto vendido. Serial: " + product.getSerialNum()
                            + " | Factura: " + saved.getInvoiceNum())
                    .build());
        }

        orderDetailRepository.saveAll(details);

        return saved;
    }

    // ── Actualizar orden (solo campos permitidos, no los productos) ──────────

    @Transactional
    public OrderClient update(Integer id, OrderClientRequest request, Integer adminId) {
        OrderClient order = findOrThrow(id);

        if (order.getDeleted())
            throw new IllegalStateException("No se puede modificar una orden eliminada");
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELED)
            throw new IllegalStateException("No se puede modificar una orden en estado: " + order.getStatus());

        if (orderClientRepository.existsByInvoiceNumAndIdNot(request.getInvoiceNum(), id))
            throw new IllegalArgumentException("Ya existe una orden con el número de factura: " + request.getInvoiceNum());

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        order.setInvoiceNum(request.getInvoiceNum());
        order.setSaleDate(request.getSaleDate());
        order.setNotes(request.getNotes());
        order.setUpdatedBy(admin);
        order.setUpdatedAt(LocalDateTime.now());

        return orderClientRepository.save(order);
    }

    // ── Cambiar estado de la orden ───────────────────────────────────────────

    @Transactional
    public OrderClient updateStatus(Integer id, OrderStatus newStatus, Integer adminId) {
        OrderClient order = findOrThrow(id);

        if (order.getDeleted())
            throw new IllegalStateException("No se puede cambiar el estado de una orden eliminada");

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        order.setStatus(newStatus);
        order.setUpdatedBy(admin);
        order.setUpdatedAt(LocalDateTime.now());

        return orderClientRepository.save(order);
    }

    // ── Cancelar orden (revierte el stock) ───────────────────────────────────

    @Transactional
    public OrderClient cancel(Integer id, Integer adminId) {
        OrderClient order = findOrThrow(id);

        if (order.getDeleted())
            throw new IllegalStateException("La orden ya está eliminada");
        if (order.getStatus() == OrderStatus.CANCELED)
            throw new IllegalStateException("La orden ya está cancelada");
        if (order.getStatus() == OrderStatus.COMPLETED)
            throw new IllegalStateException("No se puede cancelar una orden completada");

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        // Revertir estado de cada producto a AVAILABLE.
        List<OrderDetail> details = orderDetailRepository.findByOrderId(id);
        for (OrderDetail detail : details) {
            Product product = detail.getProduct();
            product.setStatus(ProductStatus.AVAILABLE);
            product.setSaleDate(null);
            productRepository.save(product);
            // El trigger recalcula model_product.status automáticamente.

            movementRepository.save(InventoryMovement.builder()
                    .eventType(MovementEventType.PRODUCT_SALE_CANCELED)
                    .product(product)
                    .order(order)
                    .admin(admin)
                    .description("Venta cancelada. Serial: " + product.getSerialNum()
                            + " | Factura: " + order.getInvoiceNum())
                    .build());
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setUpdatedBy(admin);
        order.setUpdatedAt(LocalDateTime.now());

        return orderClientRepository.save(order);
    }

    // ── Borrado lógico ───────────────────────────────────────────────────────

    @Transactional
    public void delete(Integer id, Integer adminId) {
        OrderClient order = findOrThrow(id);

        if (order.getStatus() != OrderStatus.CANCELED)
            throw new IllegalStateException("Solo se pueden eliminar órdenes canceladas");

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        order.setDeleted(true);
        order.setDeletedAt(LocalDateTime.now());
        order.setUpdatedBy(admin);
        order.setUpdatedAt(LocalDateTime.now());

        orderClientRepository.save(order);
    }

    // ── Validación de productos para nueva orden ─────────────────────────────

    private List<Product> resolveAndValidateProducts(List<Integer> productIds) {
        List<Product> products = new ArrayList<>();

        for (Integer productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Producto no encontrado con id: " + productId));

            if (product.getStatus() != ProductStatus.AVAILABLE)
                throw new IllegalStateException(
                        "El producto con serial " + product.getSerialNum() + " no está disponible");

            if (orderDetailRepository.existsByProductId(productId))
                throw new IllegalStateException(
                        "El producto con serial " + product.getSerialNum() + " ya pertenece a otra orden");

            products.add(product);
        }

        return products;
    }

    private OrderClient findOrThrow(Integer id) {
        return orderClientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden no encontrada con id: " + id));
    }
}