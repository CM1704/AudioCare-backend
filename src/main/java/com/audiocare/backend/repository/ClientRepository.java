package com.audiocare.backend.repository;

import com.audiocare.backend.model.Client;
import com.audiocare.backend.model.enums.ClientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    // Búsqueda por cédula física o jurídica (unique).
    Optional<Client> findByIdentityNumber(String identityNumber);

    // Validaciones de unicidad antes de crear/actualizar un cliente.
    boolean existsByIdentityNumber(String identityNumber);
    boolean existsByIdentityNumberAndIdNot(String identityNumber, Integer id);

    // Búsqueda por nombre para el módulo de ventas (autocompletar cliente).
    List<Client> findByNameContainingIgnoreCase(String name);

    // Filtrar clientes por tipo (PRIVATE / DISTRIBUTOR).
    List<Client> findByType(ClientType type);
}