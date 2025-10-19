package com.example.planifest.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "services")
@SQLDelete(sql = "UPDATE services SET deleted = true WHERE service_id = ?")
@Where(clause = "deleted = false")
public class PlaniService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String type;

    private boolean deleted = false; // soft Delete

    // Relación ManyToMany con eventos
    @ManyToMany(mappedBy = "planiServices")
    @JsonIgnore
    private List<Event> events = new ArrayList<>();
}
