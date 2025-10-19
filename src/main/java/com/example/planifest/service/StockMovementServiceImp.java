package com.example.planifest.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.StockMovement;
import com.example.planifest.entity.Supply;
import com.example.planifest.enums.StockStatus;
import com.example.planifest.repository.StockMovementRepository;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class StockMovementServiceImp implements Idao<StockMovement, Long> {

    private final StockMovementRepository stockMovementRepository;
    private final SupplyRepository supplyRepository;

    public StockMovementServiceImp(StockMovementRepository stockMovementRepository, SupplyRepository supplyRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.supplyRepository = supplyRepository;
    }

    @Override
    // Metodo create donde retorna en una lista
    public List<StockMovement> getAll() {
        return stockMovementRepository.findByDeletedFalse();

    }

    public long count() {
        return stockMovementRepository.count();
    }

    public Optional<StockMovement> findById(Long id) {
        return stockMovementRepository.findById(id);
    }

    /* Filtros para esta entidad */
    public List<StockMovement> filtrosMultitabla(Integer cantidad, String tipo, Long suministroId) {
        return stockMovementRepository.findAll().stream()
                .filter(m -> cantidad == null || m.getQuantity() == cantidad)
                .filter(m -> tipo == null || tipo.isBlank() || m.getType().name().equalsIgnoreCase(tipo))
                .filter(m -> suministroId == null
                        || (m.getSupply() != null && m.getSupply().getId().equals(suministroId)))
                .toList();
    }

    @Override
    public void create(StockMovement stockMovement) {
        Supply supply = stockMovement.getSupply();

        // Validar que no sea nulo
        if (supply == null || supply.getId() == null) {
            throw new IllegalArgumentException("El movimiento debe estar asociado a un insumo existente.");
        }

        // Buscar el supply actualizado
        Supply existingSupply = supplyRepository.findById(supply.getId())
                .orElseThrow(() -> new RuntimeException("Supply no encontrado"));

        int quantity = stockMovement.getQuantity();
        if (stockMovement.getType() == StockStatus.ENTRY) {
            existingSupply.setCurrentStock(existingSupply.getCurrentStock() + quantity);
        } else if (stockMovement.getType() == StockStatus.EXIT) {
            if (existingSupply.getCurrentStock() < quantity) {
                throw new RuntimeException("Stock insuficiente para salida");
            }
            existingSupply.setCurrentStock(existingSupply.getCurrentStock() - quantity);
        }

        // Guardar el stock actualizado
        supplyRepository.save(existingSupply);

        stockMovementInfoRequired(stockMovement);
        stockMovementRepository.save(stockMovement);

    }

    public StockMovement getById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("El movimiento de stock no ha sido encontrado con este id " + id));
    }

    @Override
    public void update(StockMovement stockMovement) {
        stockMovementInfoRequired(stockMovement);
        stockMovementRepository.save(stockMovement);
    }

    @Override
    public void deleteById(Long id) {
        StockMovement stockMovement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El movimiento de stock a eliminar no se ha encontrado"));
        stockMovement.setDeleted(true);
        stockMovementRepository.save(stockMovement);
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
