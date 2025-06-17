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
        if (clientService.count() > 0) {
            System.out.println("El seeder de cliente ya se había ejecutado.");
            return; 
        }

        createClient("Sleider Rodriguez", "sleider1605@gmail.com", "313298432");
        createClient("Juan Barragan", "Barraganj@gmail.com", "3143992629");
        createClient("James Riascos", "Riascosj@gmail.com", "3143932623");
        createClient("Sara Garzón", "Sara@gmail.com", "3133932683");
        createClient("Daliana Trujillo", "trujillo@gmail.com", "3103934623");

        System.out.println("El seeder de cliente fue ejecutado.");
    }

    private void createClient(String name, String email, String phone) {
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        clientService.create(client);
    }
}
