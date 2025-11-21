package com.example.planifest.entity;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.example.planifest.entity.restoreDeleted.SoftDeletable;
import com.example.planifest.enums.StockStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock_movements")
@SQLDelete(sql = "UPDATE stock_movements SET deleted = true WHERE stock_movement_id = ?")
@Where(clause = "deleted = false")
public class StockMovement implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_movement_id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private StockStatus type;

    @Column(length = 200, nullable = false)
    private String remarks;

    // Soft delete
    private boolean deleted = false;

    /* Metodos para restaurar */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Relationships
    @ManyToOne
    @JoinColumn(name = "supply_id", nullable = false)
    private Supply supply;

}