package com.example.planifest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.PlaniService;
import com.example.planifest.service.PlaniServiceImp;

@Component
@Order(3)
public class PlaniServiceSeeder implements CommandLineRunner {

    private final PlaniServiceImp planiServiceImp;

    public PlaniServiceSeeder(PlaniServiceImp planiServiceImp) {
        this.planiServiceImp = planiServiceImp;
    }

    @Override

    public void run(String... args) throws Exception {

        PlaniService service1 = new PlaniService();
        service1.setName("Mobiliario");
        service1.setDescription("Empresa Niños S.A.S no presta 100 sillas");
        service1.setType("Mobiliario");
        planiServiceImp.create(service1);

        PlaniService service2 = new PlaniService();
        service2.setName("Catering");
        service2.setDescription("La empresa Comida S.A.S");
        service2.setType("Catering");
        planiServiceImp.create(service2);

        PlaniService service3 = new PlaniService();
        service3.setName("Mobiliario");
        service3.setDescription("La empresa Muebles S.A.S");
        service3.setType("Mobiliario");
        planiServiceImp.create(service3);

        PlaniService service4 = new PlaniService();
        service4.setName("Catering");
        service4.setDescription("La empresa Logitech S.A.S");
        service4.setType("Catering");
        planiServiceImp.create(service4);

        PlaniService service5 = new PlaniService();
        service5.setName("Catering");
        service5.setDescription("La empresa Comida S.A.S");
        service5.setType("Catering");
        planiServiceImp.create(service5);

        PlaniService service6 = new PlaniService();
        service6.setName("Mobiliario");
        service6.setDescription("La empresa SillasSyR S.A.S");
        service6.setType("Mobiliario");
        planiServiceImp.create(service6);

        PlaniService service7 = new PlaniService();
        service7.setName("Catering");
        service7.setDescription("La empresa Pasteles S.A.S");
        service7.setType("Catering");
        planiServiceImp.create(service7);

        PlaniService service8 = new PlaniService();
        service8.setName("Mobiliario");
        service8.setDescription("La empresa MesasR S.A.S");
        service8.setType("Mobiliario");
        planiServiceImp.create(service8);

        PlaniService service9 = new PlaniService();
        service9.setName("Catering");
        service9.setDescription("La empresa Buffet S.A.S");
        service9.setType("Catering");
        planiServiceImp.create(service9);

        PlaniService service10 = new PlaniService();
        service10.setName("Mobilario");
        service10.setDescription("La empresa Inmuebles S.A.S");
        service10.setType("Mobiliario");
        planiServiceImp.create(service10);


        System.out.println("El Seeder de PlaniService fue ejecutado");
    }

}
