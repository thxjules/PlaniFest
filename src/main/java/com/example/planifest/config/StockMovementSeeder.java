package com.example.planifest.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.StockMovement;
import com.example.planifest.entity.Supply;
import com.example.planifest.enums.StockStatus;
import com.example.planifest.service.StockMovementServiceImp;
import com.example.planifest.service.SupplyServiceImp;

@Order(4)
@Component
public class StockMovementSeeder implements CommandLineRunner {

    private final StockMovementServiceImp stockMovementService;
    private final SupplyServiceImp supplyService;

    public StockMovementSeeder(StockMovementServiceImp stockMovementService, SupplyServiceImp supplyService) {
        this.stockMovementService = stockMovementService;
        this.supplyService = supplyService;

    }

    @Override
    public void run(String... args) throws Exception {
        if (stockMovementService.getAll().isEmpty()) {

            if (stockMovementService.count() > 0) {
                System.out.println("El Seder ya se había ejecutado");
                return;
            }

            List<Supply> supplies = supplyService.getAll();

            if (supplies.size() >= 3) {

                StockMovement stockMovement1 = new StockMovement();
                /* El 0 demuestra la posicion del supply que se usara */
                stockMovement1.setSupply(supplies.get(0));
                stockMovement1.setDate(LocalDate.now());
                stockMovement1.setQuantity(5);
                stockMovement1.setType(StockStatus.ENTRY);
                stockMovement1.setRemarks(
                        "Se daran entrada a 5 unidades mas de los Globos de Colores que estan con el ID numero 1");
                stockMovementService.create(stockMovement1);
                System.out.println("Seeder: El Stock Movement numero 1 se ha creado");


                StockMovement stockMovement2 = new StockMovement();
                /* El 0 demuestra la posicion del supply que se usara */
                stockMovement2.setSupply(supplies.get(1));
                stockMovement2.setDate(LocalDate.now());
                stockMovement2.setQuantity(10);
                stockMovement2.setType(StockStatus.ENTRY);
                stockMovement2.setRemarks(
                        "Se agragara 10 bolsas con pulseras de neon con el id numero 2");
                stockMovementService.create(stockMovement2);
                System.out.println("Seeder: El Stock Movement numero 2 se ha creado");

                 StockMovement stockMovement3 = new StockMovement();
                /* El 0 demuestra la posicion del supply que se usara */
                stockMovement3.setSupply(supplies.get(2));
                stockMovement3.setDate(LocalDate.now());
                stockMovement3.setQuantity(1);
                stockMovement3.setType(StockStatus.EXIT);
                stockMovement3.setRemarks(
                        "Se quitaran/restaran 1 Fuente de agua por motivos de fallos");
                stockMovementService.create(stockMovement3);
                System.out.println("Seeder: El Stock Movement numero 3 se ha creado");









            } else {
                System.out
                        .println("No se pudo crear ninguna Observacion ya que no existe ningun Suministro actualmente");
            }
        }
    }

}
