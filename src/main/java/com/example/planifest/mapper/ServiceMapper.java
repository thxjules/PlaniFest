package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.ServiceDTO;
import com.example.planifest.entity.Service;

@Component
public class ServiceMapper {

    private final ModelMapper modelMapper;

    public ServiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

    }

    public ServiceDTO tDto(Service service) {
        return modelMapper.map(service, ServiceDTO.class);
    }

    public Service toEntiy(ServiceDTO serviceDTO) {
        return modelMapper.map(serviceDTO, Service.class);

    }

}
