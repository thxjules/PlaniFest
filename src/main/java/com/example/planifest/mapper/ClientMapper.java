package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.ClientDTO;
import com.example.planifest.entity.Client;

@Component
public class ClientMapper {

    private final ModelMapper modelMapper;
    
    public ClientMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public ClientDTO toDto(Client client){
        return modelMapper.map(client, ClientDTO.class);
    }

    public Client toEntity(ClientDTO clientDTO){
        return modelMapper.map(clientDTO, Client.class);
    }

}
