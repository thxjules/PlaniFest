package com.example.planifest.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clients")
@SQLDelete(sql = "UPDATE clients SET deleted = true WHERE client_id = ?")
@Where(clause = "deleted = false")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    /* Soft Delete */
    private boolean deleted = false;

    @OneToMany(mappedBy = "client")
    @JsonIgnore
    private List<Event> events;
}
