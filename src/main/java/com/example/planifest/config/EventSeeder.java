package com.example.planifest.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Event;
import com.example.planifest.enums.EventStatus;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;


@Component
public class EventSeeder implements CommandLineRunner {

    private final EventServiceImp eventService;
    private final ClientServiceImp clientService;

    public EventSeeder(EventServiceImp eventService, ClientServiceImp clientService) {
        this.eventService = eventService;
        this.clientService=clientService;
    }

    @Override
    public void run(String... args) throws Exception {

        Event event1 = new Event();
        event1.setEventName("Evento de Música");
        event1.setDescription("Concierto de rock en vivo");
        event1.setDate(LocalDate.of(2025, 6, 20));
        event1.setStartTime(LocalTime.of(18, 0));
        event1.setEndTime(LocalTime.of(23, 0));
        event1.setGuestCount(150);
        event1.setLocation("Auditorio Central");
        event1.setStatus(EventStatus.ACTIVE);
    

        // Para simplificar, listas vacías
        event1.setTasks(Collections.emptyList());
        event1.setSupplies(Collections.emptyList());
        event1.setServices(Collections.emptyList());

        eventService.create(event1);

        System.out.println("Seeder: Evento creado");
    }
}
