package com.example.planifest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_supply")
public class EventSupply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "supply_id", nullable = false)
    private Supply supply;

    // Soft Delete
    private boolean deleted = false;
    /* Atributo de cantidad referente a esta tabla */
    @Column(name = "quantity_supply", nullable = false)
    private int quantitySupply;

    public EventSupply(Event event, Supply supply, int quantitySupply) {
        this.event = event;
        this.supply = supply;
        this.quantitySupply = quantitySupply;
    }

}