package com.example.planifest.service;

import java.util.List;

import com.example.planifest.entity.PlaniService;
import com.example.planifest.repository.PlaniServiceRepository;
import com.example.planifest.service.dao.Idao;
import org.springframework.stereotype.Service;

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
        planiServiceRepository.save(planiService);
    }

    @Override
    public void update(PlaniService planiService) {
        if (planiService.getId() != null && planiServiceRepository.existsById(planiService.getId())) {
            planiServiceRepository.save(planiService);
        } else {
            throw new RuntimeException("El servicio no existe.");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (planiServiceRepository.existsById(id)) {
            planiServiceRepository.deleteById(id);
        } else {
            throw new RuntimeException("El servicio no existe.");
        }
    }
    
}
