package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Supply;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class SupplyServiceImp implements Idao<Supply, Long> {

    // Sale error a menos que se cree el constructor
    private final SupplyRepository supplyRepository;

    public SupplyServiceImp(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override

    public List<Supply> getAll() {
        return supplyRepository.findAll();

    }
    
      public long count() {
        return supplyRepository.count();
    }
    

    @Override
    public void create(Supply supply) {
        supplyInfoRequired(supply);
        supplyRepository.save(supply);
    }

    @Override
    public void update(Supply supply) {
        if (supply.getId() != null && supplyRepository.existsById(supply.getId())) {
            throw new RuntimeException("No se puede actualizar el Recurso porque no existe.");
        }
        supplyInfoRequired(supply);
        supplyRepository.save(supply);
    }

    @Override
    public void deleteById(Long id) {
        if (supplyRepository.existsById(id)) {
            supplyRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el Recurso porque no existe.");
        }
    }

    private void supplyInfoRequired(Supply supply) {

        /*
         * Valida si los atributos de la tabla re recursos no son ni Nulos o Si estan
         * vacios y con espacio
         */
        if (supply.getName() == null || supply.getName().isBlank()) {
            throw new RuntimeException("El Recurso no se puede crear si no se le ingresa un nombre");
        }

        if (supply.getSupplyType() == null || supply.getSupplyType().isBlank()) {
            throw new RuntimeException("El recurso no se puede crear si no se le asigna a algun tipo que pertenesca");
        }

        if (supply.getDescription() == null || supply.getDescription().isBlank()) {
            throw new RuntimeException(
                    "El Recurso debe tener una descripcion a su vez que debe ser menos de 200 caracteres");
        }

        if (supply.getDescription().length() > 200) {
            throw new RuntimeException("La descripcion tiene que tener menos de 200 caracteres");
        }
        if (supply.getStorageLocation() == null || supply.getStorageLocation().isBlank()) {
            throw new RuntimeException("Debe existir un lugar de Almacenaje para crear el resurso");
        }

        if (supply.getStatus() == null) {
            throw new RuntimeException("El recurso debe de padecer algun estado para ser creado");
        }

        if (supply.getCurrentStock() <= 0) {
            throw new RuntimeException("La cantidad de stock No puede ser 0 ni menor a 0");
        }

        if (supply.getMinStock() <= 0 || supply.getMinStock() > supply.getMaxStock()) {
            throw new RuntimeException("El stock minimo debe ser mayor a 0 Y no puede ser mayor al Stock maximo");
        }

        if (supply.getMaxStock() <= 0 || supply.getMaxStock() < supply.getMinStock()) {
            throw new RuntimeException("El stock maximo debe ser mayor a 0 y no puede ser menor al Stock minimo");
        }

        if (supply.getPackagingUnit() == null || supply.getPackagingUnit().isBlank()) {
            throw new RuntimeException("El recurso tiene que tener la unidad de empaquetado para ser creado");
        }

    }
}
