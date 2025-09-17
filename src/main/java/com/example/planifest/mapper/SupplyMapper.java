package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.SupplyDTO;
import com.example.planifest.entity.Supply;

@Component
public class SupplyMapper {
    private final ModelMapper modelMapper;

    public SupplyMapper(ModelMapper modelMapper){
        this.modelMapper=modelMapper;
    }

    public SupplyDTO toDto (Supply supply){
        return modelMapper.map(supply, SupplyDTO.class);
    }

    public Supply toEntity (SupplyDTO supplyDTO){
        return modelMapper.map(supplyDTO, Supply.class);
    }
}
