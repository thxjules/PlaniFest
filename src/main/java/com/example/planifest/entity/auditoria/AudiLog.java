package com.example.planifest.entity.auditoria;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AudiLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String action;
    private String entityName;
    private Long entityId;
    private String performedBy; 
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist(){
        this.timestamp= LocalDateTime.now();
    }

}

