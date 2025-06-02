package com.example.planifest.service;

import java.util.List;

import com.example.planifest.entity.PlaniService;
import com.example.planifest.repository.PlaniServiceRepository;
import com.example.planifest.service.dao.Idao;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceImp implements Idao<Service, Long> {

    private final PlaniServiceRepository serviceRepository;

    public ServiceServiceImp(PlaniServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Service> getAll() {
        return serviceRepository.findAll();
    }

    @Override
    public void create(Service service) {
        serviceRepository.save(service);
    }

    @Override
    public void update(Service service) {
        if (service.getId() != null && serviceRepository.existsById(service.getId())) {
            serviceRepository.save(service);
        } else {
            throw new RuntimeException("El servicio no existe.");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
        } else {
            throw new RuntimeException("El servicio no existe.");
        }
    }
    
}
