package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.PlaniServiceDTO;
import com.example.planifest.entity.PlaniService;

@Component
public class PlaniServiceMapper {

    private final ModelMapper modelMapper;

    public PlaniServiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

    }

    public PlaniServiceDTO tDto(PlaniService service) {
        return modelMapper.map(service, PlaniServiceDTO.class);
    }

    public PlaniService toEntiy(PlaniServiceDTO serviceDTO) {
        return modelMapper.map(serviceDTO, PlaniService.class);

    }

}
