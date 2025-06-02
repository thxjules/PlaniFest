package com.example.planifest.service;

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
    //Metodo create donde retorna en una lista
    public List<StockMovement> getAll() {
        return stockMovementRepository.findAll();
    
    }
        @Override
        public void create(StockMovement stockMovement) {
            stockMovementRepository.save(stockMovement);
        }


        @Override
        public void update(StockMovement stockMovement) {
            if (stockMovement.getId() != null && stockMovementRepository.existsById(stockMovement.getId())) {
                stockMovementRepository.save(stockMovement);
            } else {
                throw new RuntimeException("No se puede actualizar el movimiento de stock porque no existe.");
            }
        }

        @Override
        public void deleteById(Long id) {
            if (stockMovementRepository.existsById(id)) {
                stockMovementRepository.deleteById(id);
            } else {
                throw new RuntimeException("No se puede eliminar el movimiento de stock porque no existe.");
            }
        }
    

}
