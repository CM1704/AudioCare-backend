package com.audiocare.backend.repository;

import com.audiocare.backend.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Obtener todos los detalles (productos) de una orden específica.
    List<OrderDetail> findByOrderId(Integer orderId);

    // Verificar si un producto ya está asociado a alguna orden.
    // Usado como validación antes de agregar un producto a una nueva venta.
    boolean existsByProductId(Integer productId);

    // Obtener el detalle que contiene un producto específico.
    // Útil para saber en qué orden fue vendido un producto dado su serial.
    Optional<OrderDetail> findByProductId(Integer productId);
}