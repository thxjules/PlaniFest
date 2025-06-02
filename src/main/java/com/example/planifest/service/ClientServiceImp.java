package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Client;
import com.example.planifest.repository.ClientRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class ClientServiceImp implements Idao<Client, Long> {

    private final ClientRepository clientRepository;

    public ClientServiceImp(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public void create(Client client) {
        clientRepository.save(client);
    }

    @Override
    public void update(Client client) {
        if (client.getId() != null && clientRepository.existsById(client.getId())) {
            clientRepository.save(client);
        } else {
            throw new RuntimeException("El cliente no existe.");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
        } else {
            throw new RuntimeException("El cliente no existe.");
        }
    }
    
}
