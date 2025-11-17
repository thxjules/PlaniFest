package com.example.planifest.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.example.planifest.entity.restoreDeleted.SoftDeletable;
import com.example.planifest.enums.SupplyStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "suplies")
@SQLDelete(sql = "UPDATE tasks SET deleted = true WHERE task_id = ?")
@Where(clause = "deleted = false")
public class Supply implements SoftDeletable {

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

    /* Soft delete */
    private boolean deleted = false;

    /* Metodo para restaurar */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @OneToMany(mappedBy = "supply")
    private List<EventSupply> eventSupplies = new ArrayList<>();

    @OneToMany(mappedBy = "supply")
    @JsonIgnore
    private List<StockMovement> stockMovements;

}