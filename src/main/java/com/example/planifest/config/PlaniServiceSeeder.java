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

        if (planiServiceImp.count() > 0) {
            System.out.println("El Seder ya se había ejecutado");
            return;
        }

        PlaniService service1 = new PlaniService();
        service1.setName("Eventos Infantiles S.A.S");
        service1.setDescription("Presta 100 sillas para fiestas infantiles");
        service1.setType("Mobiliario");
        planiServiceImp.create(service1);

        PlaniService service2 = new PlaniService();
        service2.setName("Delicias Gourmet S.A.S");
        service2.setDescription("Presta el servicio de catering con menú gourmet");
        service2.setType("Catering");
        planiServiceImp.create(service2);

        PlaniService service3 = new PlaniService();
        service3.setName("Muebles Rápidos S.A.S");
        service3.setDescription("Provee 100 mesas plegables para eventos");
        service3.setType("Mobiliario");
        planiServiceImp.create(service3);

        PlaniService service4 = new PlaniService();
        service4.setName("LogiCatering S.A.S");
        service4.setDescription("Presta el servicio de catering empresarial");
        service4.setType("Catering");
        planiServiceImp.create(service4);

        PlaniService service5 = new PlaniService();
        service5.setName("Sabores y Eventos S.A.S");
        service5.setDescription("Presta el servicio de catering para bodas y reuniones");
        service5.setType("Catering");
        planiServiceImp.create(service5);

        PlaniService service6 = new PlaniService();
        service6.setName("Sillas SyR S.A.S");
        service6.setDescription("Presta 200 sillas de lujo para eventos ejecutivos");
        service6.setType("Mobiliario");
        planiServiceImp.create(service6);

        PlaniService service7 = new PlaniService();
        service7.setName("Dulce Tentación S.A.S");
        service7.setDescription("Provee 400 cupcakes personalizados para el evento");
        service7.setType("Catering");
        planiServiceImp.create(service7);

        PlaniService service8 = new PlaniService();
        service8.setName("Mesas & Co S.A.S");
        service8.setDescription("Provee mesas rectangulares y redondas para eventos");
        service8.setType("Mobiliario");
        planiServiceImp.create(service8);

        PlaniService service9 = new PlaniService();
        service9.setName("Buffet Central S.A.S");
        service9.setDescription("Ofrece servicio de buffet con platos típicos");
        service9.setType("Catering");
        planiServiceImp.create(service9);

        PlaniService service10 = new PlaniService();
        service10.setName("Inmuebles Eventos S.A.S");
        service10.setDescription("Presta mobiliario completo para ferias y exposiciones");
        service10.setType("Mobiliario");
        planiServiceImp.create(service10);

        System.out.println("El Seeder de PlaniService fue ejecutado");
    }

}
