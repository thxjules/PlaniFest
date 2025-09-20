package com.example.planifest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Supply;
import com.example.planifest.enums.SupplyStatus;
import com.example.planifest.service.SupplyServiceImp;

@Component
@Order(2)
public class SupplySeeder implements CommandLineRunner {

    private final SupplyServiceImp supplyService;

    /* Constructor */
    public SupplySeeder(SupplyServiceImp supplyService) {
        this.supplyService = supplyService;
    }

    @Override
    /* Creacion de los Seeders */
    public void run(String... args) throws Exception {
        if (supplyService.count() > 0) {
            System.out.println("El seder de suministros ya había sido ejecutado");
             return; 
        }
        
        if (supplyService.getAll().isEmpty()) {

            Supply supply1 = new Supply();
            supply1.setName("Globos de Colores");
            supply1.setSupplyType("Decoracion");
            supply1.setDescription(
                    "Advertencia: Use todos los Globos que estan el Paquete, Lo guarde los Paquetes si no estan Todos los Globos");
            supply1.setStorageLocation("Almacen numero 1");
            supply1.setStatus(SupplyStatus.AVAILABLE);
            supply1.setCurrentStock(40);
            supply1.setMinStock(5);
            supply1.setMaxStock(150);
            supply1.setPackagingUnit("Cajas de Espuma");

            /* Creacion del supply 1 */
            supplyService.create(supply1);
            System.out.println("Seeder: El suministro 1 ha sido creado");

            Supply supply2 = new Supply();
            supply2.setName("Pulseras de Neon");
            supply2.setSupplyType("Mobiliario");
            supply2.setDescription(
                    "Solo usar estas pulseras en eventos Nocturnos, de cualquier tipo para una mejor experiencia Nt(Se cuenta cada pulsera)");
            supply2.setStorageLocation("Almacen numero 1");
            supply2.setStatus(SupplyStatus.AVAILABLE);
            supply2.setCurrentStock(60);
            supply2.setMinStock(5);
            supply2.setMaxStock(200);
            supply2.setPackagingUnit("Bolsas de plastico");

            supplyService.create(supply2);
            System.out.println("Seeder: El suministro 2 ha sido creado");

            Supply supply3 = new Supply();
            supply3.setName("Fuentes de agua");
            supply3.setSupplyType("Mobiliario");
            supply3.setDescription(
                    "Precaucion: Uso de las fuentes de agua SOLO en eventos donde haya prescencia de Adultos y no eventos que tengan ningun tipo de menor de edad");
            supply3.setStorageLocation("Almacen numero 2");
            supply3.setStatus(SupplyStatus.AVAILABLE);
            supply3.setCurrentStock(20);
            supply3.setMinStock(2);
            supply3.setMaxStock(50);
            supply3.setPackagingUnit("No tiene");

            supplyService.create(supply3);
            System.out.println("Seeder: El suministro 2 ha sido creado");

            Supply supply4 = new Supply();
            supply4.setName("Maquinas de humo");
            supply4.setSupplyType("Mobiliario");
            supply4.setDescription("Tener cuidado a la hora de depositar las maquinas de humo en cajas de espama");
            supply4.setStorageLocation("Almacen numero 2");
            supply4.setStatus(SupplyStatus.AVAILABLE);
            supply4.setCurrentStock(60);
            supply4.setMinStock(8);
            supply4.setMaxStock(140);
            supply4.setPackagingUnit("Cajas de Espuma");

            supplyService.create(supply4);
            System.out.println("Seeder: El suministro 2 ha sido creado");

        }
    }

}