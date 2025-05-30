package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.UserDTO;
import com.example.planifest.entity.User;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserMapper (ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    public UserDTO toDTO (User user){
        return modelMapper.map(user, UserDTO.class);
    }

    public User toEntity(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
