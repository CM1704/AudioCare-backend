package com.audiocare.backend.service;

import com.audiocare.backend.dto.request.ClientRequest;
import com.audiocare.backend.model.Client;
import com.audiocare.backend.model.enums.ClientType;
import com.audiocare.backend.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Integer id) {
        return findOrThrow(id);
    }

    public List<Client> findByName(String name) {
        return clientRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Client> findByType(ClientType type) {
        return clientRepository.findByType(type);
    }

    @Transactional
    public Client create(ClientRequest request) {
        if (clientRepository.existsByIdentityNumber(request.getIdentityNumber()))
            throw new IllegalArgumentException("Ya existe un cliente con ese número de identidad");

        Client client = Client.builder()
                .identityNumber(request.getIdentityNumber())
                .name(request.getName())
                .lastName1(request.getLastName1())
                .lastName2(request.getLastName2())
                .type(request.getType())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return clientRepository.save(client);
    }

    @Transactional
    public Client update(Integer id, ClientRequest request) {
        Client client = findOrThrow(id);

        if (clientRepository.existsByIdentityNumberAndIdNot(request.getIdentityNumber(), id))
            throw new IllegalArgumentException("Ya existe un cliente con ese número de identidad");

        client.setIdentityNumber(request.getIdentityNumber());
        client.setName(request.getName());
        client.setLastName1(request.getLastName1());
        client.setLastName2(request.getLastName2());
        client.setType(request.getType());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        return clientRepository.save(client);
    }

    @Transactional
    public void delete(Integer id) {
        // RESTRICT en FK: fallará si el cliente tiene órdenes registradas.
        // El GlobalExceptionHandler captura DataIntegrityViolationException
        // y devuelve un mensaje claro al frontend.
        clientRepository.delete(findOrThrow(id));
    }

    private Client findOrThrow(Integer id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con id: " + id));
    }
}