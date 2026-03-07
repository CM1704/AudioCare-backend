package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.ProductRequest;
import com.audiocare.backend.model.*;
import com.audiocare.backend.model.enums.MovementEventType;
import com.audiocare.backend.model.enums.ProductStatus;
import com.audiocare.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelProductRepository modelProductRepository;
    private final SupplierOrderRepository supplierOrderRepository;
    private final AdminRepository adminRepository;
    private final InventoryMovementRepository movementRepository;

    public List<Product> findAll() {
        // Ordenados por entry_date ASC para visualización FIFO por defecto.
        return productRepository.findByStatusOrderByEntryDateAsc(ProductStatus.AVAILABLE);
    }

    public List<Product> findAllIncludingBilled() {
        return productRepository.findAll();
    }

    public Product findById(Integer id) {
        return findOrThrow(id);
    }

    public Optional<Product> findBySerialNum(String serialNum) {
        return productRepository.findBySerialNum(serialNum);
    }

    public List<Product> findByModel(Integer modelId, ProductStatus status) {
        return productRepository.findByModelIdAndStatusOrderByEntryDateAsc(modelId, status);
    }

    public List<Product> findBySupplierOrder(Integer supplierOrderId) {
        return productRepository.findBySupplierOrderIdOrderByEntryDateAsc(supplierOrderId);
    }

    // Devuelve el producto AVAILABLE más antiguo de un modelo (lógica FIFO).
    // Usado por el frontend para sugerir qué unidad vender primero.
    public Optional<Product> findOldestAvailable(Integer modelId) {
        return productRepository.findOldestAvailableByModel(modelId);
    }

    @Transactional
    public Product create(ProductRequest request, Integer adminId) {
        if (productRepository.existsBySerialNum(request.getSerialNum()))
            throw new IllegalArgumentException("Ya existe un producto con el serial: " + request.getSerialNum());

        ModelProduct model = modelProductRepository.findById(request.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("Modelo no encontrado con id: " + request.getModelId()));

        SupplierOrder supplierOrder = supplierOrderRepository.findById(request.getSupplierOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido de proveedor no encontrado con id: " + request.getSupplierOrderId()));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado"));

        Product product = Product.builder()
                .serialNum(request.getSerialNum())
                .model(model)
                .supplierOrder(supplierOrder)
                .entryDate(request.getEntryDate())
                .status(ProductStatus.AVAILABLE)
                .createdBy(admin)
                .build();

        Product saved = productRepository.save(product);
        // El trigger de DB actualiza model_product.status a AVAILABLE automáticamente.

        // Registrar evento en el log de movimientos.
        movementRepository.save(InventoryMovement.builder()
                .eventType(MovementEventType.PRODUCT_ADDED)
                .product(saved)
                .supplierOrder(supplierOrder)
                .admin(admin)
                .description("Producto registrado. Serial: " + saved.getSerialNum()
                        + " | Modelo: " + model.getName())
                .build());

        return saved;
    }

    @Transactional
    public Product update(Integer id, ProductRequest request) {
        Product product = findOrThrow(id);

        if (product.getStatus() == ProductStatus.BILLED)
            throw new IllegalStateException("No se puede modificar un producto ya facturado");

        if (productRepository.existsBySerialNum(request.getSerialNum())
                && !product.getSerialNum().equals(request.getSerialNum()))
            throw new IllegalArgumentException("Ya existe un producto con el serial: " + request.getSerialNum());

        ModelProduct model = modelProductRepository.findById(request.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("Modelo no encontrado con id: " + request.getModelId()));

        SupplierOrder supplierOrder = supplierOrderRepository.findById(request.getSupplierOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido de proveedor no encontrado con id: " + request.getSupplierOrderId()));

        product.setSerialNum(request.getSerialNum());
        product.setModel(model);
        product.setSupplierOrder(supplierOrder);
        product.setEntryDate(request.getEntryDate());

        return productRepository.save(product);
    }

    @Transactional
    public void delete(Integer id) {
        Product product = findOrThrow(id);

        if (product.getStatus() == ProductStatus.BILLED)
            throw new IllegalStateException("No se puede eliminar un producto ya facturado");

        productRepository.delete(product);
        // El trigger recalcula model_product.status tras el delete.
    }

    private Product findOrThrow(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con id: " + id));
    }
}