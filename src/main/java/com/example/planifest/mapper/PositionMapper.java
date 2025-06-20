package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.PositionDTO;
import com.example.planifest.entity.Position;

@Component
public class PositionMapper {

    private final ModelMapper modelMapper;

    public PositionMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public PositionDTO toDto(Position position){
        return modelMapper.map(position, PositionDTO.class);
    }

    public Position toEntity(PositionDTO positionDTO){
        return modelMapper.map(positionDTO, Position.class);
    }

}
