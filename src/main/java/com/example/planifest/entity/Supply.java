package com.example.planifest.entity;

import java.util.List;

import com.example.planifest.enums.SupplyStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "suplies")
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "supply_type", nullable = false, length = 50)
    private String supplyType;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "storage_location", nullable = false, length = 50)
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SupplyStatus status;

    @Column(name = "current_stock", nullable = false)
    private int currentStock;

    @Column(name = "min_stock", nullable = false)
    private int minStock;

    @Column(name = "max_stock", nullable = false)
    private int maxStock;

    @Column(name = "packaging_unit", nullable = false, length = 50)
    private String packagingUnit;

    // Relationship

    @ManyToMany(mappedBy = "supplies")
    private List<Event> events;

    @OneToMany(mappedBy = "supply")
    private List<StockMovement> stockMovements;

}