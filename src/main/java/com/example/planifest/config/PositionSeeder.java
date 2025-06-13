package com.example.planifest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Position;
import com.example.planifest.service.PositionServiceImpl;

@Component
@Order(8)
public class PositionSeeder implements CommandLineRunner {

    private final PositionServiceImpl positionService;

    public PositionSeeder(PositionServiceImpl positionService) {
        this.positionService = positionService;
    }

    @Override
    public void run(String... args) throws Exception {

        Position position1 = new Position();
        position1.setName("Mesero");
        positionService.create(position1);

        Position position2 = new Position();
        position2.setName("Chef");
        positionService.create(position2);

        Position position3 = new Position();
        position3.setName("Decorador");
        positionService.create(position3);

        Position position4 = new Position();
        position4.setName("Cordinador");
        positionService.create(position4);

         System.out.println("Seeder de Position Ejecutado");
    }
    
   

}
