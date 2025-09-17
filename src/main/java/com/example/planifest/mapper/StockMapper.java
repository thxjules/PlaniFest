package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.StockDTO;
import com.example.planifest.entity.StockMovement;

@Component
public class StockMapper {

    private final ModelMapper modelMapper;

    public StockMapper (ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public StockDTO toDto (StockMovement stock){
        return modelMapper.map(stock, StockDTO.class );
    }

    public StockMovement toEntity(StockDTO stockDTO){
        return modelMapper.map(stockDTO, StockMovement.class);
    }

}
