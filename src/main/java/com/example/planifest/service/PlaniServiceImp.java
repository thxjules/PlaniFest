package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.PlaniService;
import com.example.planifest.repository.PlaniServiceRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class PlaniServiceImp implements Idao<PlaniService, Long> {

    private final PlaniServiceRepository planiServiceRepository;

    public PlaniServiceImp(PlaniServiceRepository planiServiceRepository) {
        this.planiServiceRepository = planiServiceRepository;
    }

    @Override
    public List<PlaniService> getAll() {
        return planiServiceRepository.findAll();
    }

    @Override
    public void create(PlaniService planiService) {
        validatePlaniService(planiService);
        planiServiceRepository.save(planiService);
    }

    @Override
    public void update(PlaniService planiService) {
        if (planiService.getServiceId() == null || !planiServiceRepository.existsById(planiService.getServiceId())) {
            throw new RuntimeException("No se puede actualizar el servicio porque no existe.");
        }
        validatePlaniService(planiService);
        planiServiceRepository.save(planiService);
    }

    @Override
    public void deleteById(Long id) {
        if (!planiServiceRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar el servicio porque no existe.");
        }
        planiServiceRepository.deleteById(id);
    }

    private void validatePlaniService(PlaniService planiService) {
        if (isBlank(planiService.getName())) {
            throw new RuntimeException("El nombre del servicio es obligatorio.");
        }
        if (planiService.getName().length() > 50) {
            throw new RuntimeException("El nombre del servicio no puede superar los 50 caracteres.");
        }
        if (planiService.getDescription() != null && planiService.getDescription().length() > 500) {
            throw new RuntimeException("La descripción del servicio no puede superar los 500 caracteres.");
        }
        if (planiService.getType() != null && planiService.getType().length() > 500) {
            throw new RuntimeException("El tipo del servicio no puede superar los 500 caracteres.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}
