package com.example.planifest.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.StockMovement;
import com.example.planifest.repository.StockMovementRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class StockMovementServiceImp implements Idao<StockMovement, Long> {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementServiceImp(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @Override
    // Metodo create donde retorna en una lista
    public List<StockMovement> getAll() {
        return stockMovementRepository.findAll();

    }
    
    public long count(){
        return stockMovementRepository.count();
    }

    @Override
    public void create(StockMovement stockMovement) {
        stockMovementInfoRequired(stockMovement);
        stockMovementRepository.save(stockMovement);

    }

    @Override
    public void update(StockMovement stockMovement) {
        if (stockMovement.getId() != null && stockMovementRepository.existsById(stockMovement.getId())) {
        } else {
            throw new RuntimeException("No se puede actualizar el movimiento de stock porque no existe.");
        }
        stockMovementInfoRequired(stockMovement);
        stockMovementRepository.save(stockMovement);
    }

    @Override
    public void deleteById(Long id) {
        if (stockMovementRepository.existsById(id)) {
            stockMovementRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el movimiento de stock porque no existe.");
        }
    }

    private void stockMovementInfoRequired(StockMovement stockMovement) {

        if (stockMovement.getDate() == null) {
            throw new RuntimeException("La fecha de creacion del movimiento del stock no puede ser nula");
        }


        if (stockMovement.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("La fecha del reporte de stock no pertenece a la fecha del dia de hoy");
        }

        if (stockMovement.getQuantity() <= 0) {
            throw new RuntimeException("La cantidad del movimiento stock debe ser mayor a cero, no Igual");
        }

        if (stockMovement.getType() == null) {
            throw new RuntimeException("El tipo del movimiento que se esta haciendo no debe ser nulo ");
        }

        if (stockMovement.getRemarks() == null || stockMovement.getRemarks().isBlank()) {
            throw new RuntimeException(
                    "El movimiento Stock que estas creando no puede estar vacio, nesesitas una descripcion");
        }

        if (stockMovement.getRemarks().length() > 200) {
            throw new RuntimeException(
                    "La descripcion de la observacion tiene que ser menos de 200 caracteres para ser creada");
        }

    }

}
