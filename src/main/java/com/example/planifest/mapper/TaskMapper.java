package com.example.planifest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.planifest.dto.TaskDTO;
import com.example.planifest.entity.Task;

@Component
public class TaskMapper {
     private final ModelMapper modelMapper;

     public TaskMapper (ModelMapper modelMapper){
        this.modelMapper=modelMapper;
     }

     public TaskDTO tDto (Task task){
        return modelMapper.map(task, TaskDTO.class);
     }

     public Task toEntity(TaskDTO taskDTO){
        return modelMapper.map(taskDTO, Task.class);
     }
}
