package com.example.planifest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.planifest.Exceptions.SupplyDeletionException;
import com.example.planifest.entity.Event;
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

    /* Filtros para insumos */

    public List<Supply> filtrosMultiTabla(String nombre, String lugarAlmacen, Integer stockActual ){
        return supplyRepository.findAll().stream()
        .filter(s -> nombre == null || nombre.isBlank() || s.getName().toLowerCase().contains(nombre.toLowerCase())) 
        .filter(s -> lugarAlmacen == null || lugarAlmacen.isBlank() || s.getStorageLocation().toLowerCase().contains(lugarAlmacen.toLowerCase()))
        .filter(s -> stockActual == null || s.getCurrentStock() == stockActual)
        .toList();

    }
    
      public long count() {
        return supplyRepository.count();
    }
    
    /* Encontrar por Id */
    public Optional<Supply> findById(Long id){
        return supplyRepository.findById(id);
    }

    @Override
    public void create(Supply supply) {
        supplyInfoRequired(supply);
        supplyRepository.save(supply);
    }


    @Override
    public void update(Supply supply) {
        supplyInfoRequired(supply);
        supplyRepository.save(supply);
    }

    @Override
    public void deleteById(Long id) {

         Optional<Supply> optionalSupply = supplyRepository.findById(id);

    if (optionalSupply.isEmpty()) {
        throw new RuntimeException("No se puede eliminar el recurso porque no existe.");
    }

    Supply supply = optionalSupply.get();

    try {
    // Eliminar relaciones con eventos (desvincular desde eventSupplies)
    supply.getEventSupplies().forEach(eventSupply -> {
        Event event = eventSupply.getEvent();
        event.getEventSupplies().remove(eventSupply);
    });
    supply.getEventSupplies().clear();
    supplyRepository.save(supply);

    // Eliminar el suministro
    supplyRepository.deleteById(id);
} catch (Exception e) {
    throw new SupplyDeletionException("No se puede eliminar el suministro porque está vinculado a eventos.");
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

        if (supply.getCurrentStock() <= 0 || supply.getCurrentStock() > supply.getMaxStock()) {
            throw new RuntimeException("El stock actual no puede ser mayor al stock minimo");
        }

        if (supply.getCurrentStock() <= 0 || supply.getCurrentStock() < supply.getMinStock()) {
            throw new RuntimeException("El stock Actual no puede ser menor al stock minimo");
        }

        if (supply.getPackagingUnit() == null || supply.getPackagingUnit().isBlank()) {
            throw new RuntimeException("El recurso tiene que tener la unidad de empaquetado para ser creado");
        }

    }
}
