package com.example.planifest.dto;
import java.time.LocalDate;

import com.example.planifest.enums.TaskStatus;

import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private Long userId; 
    private Long eventId; 
    private String eventName;
    private String eventLocation;
    private LocalDate eventDate;

}
