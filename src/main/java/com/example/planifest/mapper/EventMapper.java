package com.example.planifest.mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.EventDTO;
import com.example.planifest.entity.Event;

@Component
public class EventMapper {
    private final ModelMapper modelMapper;

    public EventMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EventDTO toDto(Event event) {
        return modelMapper.map(event, EventDTO.class);
    }

    public Event toEntity(EventDTO eventDTO) {
        return modelMapper.map(eventDTO, Event.class);

    }

}
