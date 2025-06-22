package com.example.planifest.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.planifest.enums.EventStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Long id;

    @Column(name = "event_name", nullable = false, length = 50)
    private String eventName;

    @Column(nullable = false, length = 200)
    private String description;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(nullable = false, length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    // Relación con Cliente
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Relación con Tareas
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Task> tasks;

    // Relación con Insumos
    @ManyToMany
    @JoinTable(name = "event_supply", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "supply_id"))
    private List<Supply> supplies;

    @ManyToMany
    @JoinTable(name = "event_service", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<PlaniService> services;
}
