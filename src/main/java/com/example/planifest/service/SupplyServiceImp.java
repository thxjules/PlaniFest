package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Supply;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class SupplyServiceImp implements Idao<Supply, Long> {

    // Sale error amenos que se cree el constructor
    private final SupplyRepository supplyRepository;

    public SupplyServiceImp(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override

    public List<Supply> getAll() {
        return supplyRepository.findAll();

    }

    @Override
    public void create(Supply supply) {
        supplyRepository.save(supply);
    }

    @Override
    public void update(Supply supply) {
        if (supply.getId() != null && supplyRepository.existsById(supply.getId())) {
            supplyRepository.save(supply);
        } else {
            throw new RuntimeException("No se puede actualizar el Recurso porque no existe.");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (supplyRepository.existsById(id)) {
            supplyRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el Recurso porque no existe.");
        }
    }

}
