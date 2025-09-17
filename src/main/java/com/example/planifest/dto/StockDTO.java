package com.example.planifest.dto;
import java.time.LocalDate;

import com.example.planifest.enums.StockStatus;

import lombok.Data;

@Data
public class StockDTO {

    private Long id;
    private LocalDate date;
    private int quantity;
    private StockStatus type;
    private String remarks;
    private Long supplyId; 

}
