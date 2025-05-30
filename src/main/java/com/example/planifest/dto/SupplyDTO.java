package com.example.planifest.dto;
import com.example.planifest.enums.SupplyStatus;

import lombok.Data;

@Data
public class SupplyDTO {

    private Long id;
    private String name;
    private String supplyType;
    private String description;
    private String storageLocation;
    private SupplyStatus status;
    private int currentStock;
    private int minStock;
    private int maxStock;
    private String packagingUnit;

}
