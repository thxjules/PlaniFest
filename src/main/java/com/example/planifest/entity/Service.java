package com.example.planifest.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false, length =50)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String type;

    @ManyToMany(mappedBy = "services")
    private List<Event> events;
}
