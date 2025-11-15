package com.example.planifest.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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

    /* Encontrar por ID */
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    /* Filtro para empleados */
    public List<Client> filtroNombreEmail(String nombre, String email) {
        return clientRepository.findAll().stream()
                .filter(c -> nombre == null || nombre.isBlank() || c.getName().toLowerCase().contains(nombre.toLowerCase()))
                .filter(c -> email == null || email.isBlank() || c.getEmail().toLowerCase().contains(email.toLowerCase()))
                .toList();
    }

    @Override
    public void create(Client client) {
        validateClient(client, true);
        clientRepository.save(client);
    }

    public long count() {
        return clientRepository.count();
    }

    @Override
    public void update(Client client) {
        if (client.getId() == null || !clientRepository.existsById(client.getId())) {
            throw new RuntimeException("El cliente no existe.");
        }
        validateClient(client, false);
        clientRepository.save(client);
    }

    @Override
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("El cliente no existe.");
        }
        clientRepository.deleteById(id);
    }

    /* 🔍 Validación general del cliente */
    private void validateClient(Client client, boolean isCreate) {
        // Validar nombre
        if (isBlank(client.getName())) {
            throw new RuntimeException("El nombre del cliente es obligatorio.");
        }
        if (client.getName().length() > 100) {
            throw new RuntimeException("El nombre del cliente no puede superar los 100 caracteres.");
        }

        // 💡 Nueva validación: solo letras y espacios (sin números ni signos)
        if (!isValidName(client.getName())) {
            throw new RuntimeException("El nombre solo puede contener letras y espacios (sin números ni símbolos).");
        }

        // Validar correo
        if (isBlank(client.getEmail())) {
            throw new RuntimeException("El correo del cliente es obligatorio.");
        }
        if (client.getEmail().length() > 100) {
            throw new RuntimeException("El correo no puede superar los 100 caracteres.");
        }
        if (!isValidEmail(client.getEmail())) {
            throw new RuntimeException("El formato del correo es inválido.");
        }
        if (isCreate && clientRepository.existsByEmailIgnoreCase(client.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con ese correo.");
        }

        // Validar teléfono
        if (client.getPhone() != null && client.getPhone().length() > 20) {
            throw new RuntimeException("El teléfono no puede superar los 20 caracteres.");
        }
        if (client.getPhone() != null && !client.getPhone().matches("\\d+")) {
            throw new RuntimeException("El teléfono solo puede contener números.");
        }

    }

    /* 📘 Métodos auxiliares */
    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }

    /* ✅ Nueva validación: solo letras y espacios */
    private boolean isValidName(String name) {
        // Solo letras (mayúsculas/minúsculas), acentos, ñ y espacios
        String regex = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$";
        return Pattern.matches(regex, name);
    }
}
