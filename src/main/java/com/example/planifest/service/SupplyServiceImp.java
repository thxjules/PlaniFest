package com.example.planifest.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Supply;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class SupplyServiceImp implements Idao<Supply, Long> {

    private final SupplyRepository supplyRepository;

    public SupplyServiceImp(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Override
    public List<Supply> getAll() {
        /* Retornaa los suministros que no esten marcados como eliminados */
        return supplyRepository.findByDeletedFalse();
    }

    /* Filtros para insumos */
    public List<Supply> filtrosMultiTabla(String nombre, String lugarAlmacen, Integer stockActual) {
        return supplyRepository.findByDeletedFalse().stream()
                .filter(s -> nombre == null || nombre.isBlank()
                        || s.getName().toLowerCase().contains(nombre.toLowerCase()))
                .filter(s -> lugarAlmacen == null || lugarAlmacen.isBlank()
                        || s.getStorageLocation().toLowerCase().contains(lugarAlmacen.toLowerCase()))
                .filter(s -> stockActual == null || s.getCurrentStock() == stockActual)
                .toList();
    }

    public long count() {
        return supplyRepository.count();
    }

    /* Encontrar por Id */
    public Optional<Supply> findById(Long id) {
        return supplyRepository.findById(id)
                .filter(s -> !s.isDeleted());
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
        if (supplyRepository.existsById(id)) {
            supplyRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar la tarea porque no existe.");
        }
    }

    private void supplyInfoRequired(Supply supply) {
        if (supply.getName() == null || supply.getName().isBlank()) {
            throw new RuntimeException("El recurso no se puede crear si no se le ingresa un nombre");
        }

        if (supply.getSupplyType() == null || supply.getSupplyType().isBlank()) {
            throw new RuntimeException("El recurso no se puede crear si no se le asigna a algún tipo que pertenezca");
        }

        if (supply.getDescription() == null || supply.getDescription().isBlank()) {
            throw new RuntimeException("El recurso debe tener una descripción de máximo 200 caracteres");
        }

        if (supply.getDescription().length() > 200) {
            throw new RuntimeException("La descripción tiene que tener menos de 200 caracteres");
        }

        if (supply.getStorageLocation() == null || supply.getStorageLocation().isBlank()) {
            throw new RuntimeException("Debe existir un lugar de almacenaje para crear el recurso");
        }

        if (supply.getStatus() == null) {
            throw new RuntimeException("El recurso debe tener un estado definido");
        }

        if (supply.getCurrentStock() <= 0) {
            throw new RuntimeException("La cantidad de stock no puede ser 0 ni menor a 0");
        }

        if (supply.getMinStock() <= 0 || supply.getMinStock() > supply.getMaxStock()) {
            throw new RuntimeException("El stock mínimo debe ser mayor a 0 y no puede superar al stock máximo");
        }

        if (supply.getMaxStock() <= 0 || supply.getMaxStock() < supply.getMinStock()) {
            throw new RuntimeException("El stock máximo debe ser mayor a 0 y no puede ser menor al stock mínimo");
        }

        if (supply.getCurrentStock() > supply.getMaxStock()) {
            throw new RuntimeException("El stock actual no puede ser mayor al stock máximo");
        }

        if (supply.getCurrentStock() < supply.getMinStock()) {
            throw new RuntimeException("El stock actual no puede ser menor al stock mínimo");
        }

        if (supply.getPackagingUnit() == null || supply.getPackagingUnit().isBlank()) {
            throw new RuntimeException("El recurso debe tener una unidad de empaquetado");
        }
    }

    public Map<String, Integer> obtenerStockAgrupadoPorTipo() {
        List<Object[]> results = supplyRepository.countSuppliesByType();
        Map<String, Integer> stockPorTipo = new HashMap<>();

        for (Object[] row : results) {
            String tipo = (String) row[0];
            Integer cantidad = ((Long) row[1]).intValue(); // COUNT devuelve Long
            stockPorTipo.put(tipo, cantidad);
        }

        return stockPorTipo;
    }
}
