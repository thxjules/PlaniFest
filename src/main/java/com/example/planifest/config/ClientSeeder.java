package com.example.planifest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Client;
import com.example.planifest.service.ClientServiceImp;

@Component
@Order(1)
public class ClientSeeder implements CommandLineRunner {
    private final ClientServiceImp clientService;

    public ClientSeeder(ClientServiceImp clientService) {
        this.clientService = clientService;
    }

    @Override

    public void run(String... args) throws Exception {
        Client client1 = new Client();

        client1.setName("Sleider Rodriguez");
        client1.setEmail("sleider1605@gmail.com");
        client1.setPhone("313298432");
        clientService.create(client1);

        Client client2 = new Client();

        client2.setName("Juan Barragan");
        client2.setEmail("Barraganj@gmail.com");
        client2.setPhone("3143992629");
        clientService.create(client2);

        Client client3 = new Client();

        client3.setName("James Riascos");
        client3.setEmail("Riascosj@gmail.com");
        client3.setPhone("3143932623");
        clientService.create(client3);

         Client client4 = new Client();

        client4.setName("Sara Garzón");
        client4.setEmail("Sara@gmail.com");
        client4.setPhone("3133932683");
        clientService.create(client4);

         Client client5 = new Client();

        client5.setName("Daliana Trujillo");
        client5.setEmail("trujillo@gmail.com");
        client5.setPhone("3103934623");
        clientService.create(client5);

        System.out.println("El seeder de cliente fue ejecutado");
    

        }

    }


