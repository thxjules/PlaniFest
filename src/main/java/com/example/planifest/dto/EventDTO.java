package com.example.planifest.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

@Data
public class EventDTO {

    private Long id;
    private String eventName;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer guestCount;
    private String location;
}
