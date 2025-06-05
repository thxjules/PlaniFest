package com.example.planifest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Position;
import com.example.planifest.repository.PositionRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class PositionServiceImpl implements Idao<Position, Long> {

    private final PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    @Override
    public void create(Position position) {
        validatePosition(position);
        positionRepository.save(position);
    }

    @Override
    public void update(Position position) {
        if (position.getId() == null || !positionRepository.existsById(position.getId())) {
            throw new RuntimeException("No se puede actualizar una posición que no existe.");
        }
        validatePosition(position);
        positionRepository.save(position);
    }

    @Override
    public void deleteById(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new RuntimeException("No se puede borrar una posición que no existe.");
        }
        positionRepository.deleteById(id);
    }

    private void validatePosition(Position position) {
        if (position.getName() == null || position.getName().isBlank()) {
            throw new RuntimeException("El nombre de la posición es obligatorio.");
        }
    }
}