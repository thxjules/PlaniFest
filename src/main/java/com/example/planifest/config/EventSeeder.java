package com.example.planifest.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Client;
import com.example.planifest.entity.Event;
import com.example.planifest.entity.PlaniService;
import com.example.planifest.entity.Supply;
import com.example.planifest.enums.EventStatus;
import com.example.planifest.service.ClientServiceImp;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.PlaniServiceImp;
import com.example.planifest.service.SupplyServiceImp;

@Component
@Order(5)
public class EventSeeder implements CommandLineRunner {

    private final EventServiceImp eventService;
    private final ClientServiceImp clientService;
    private final SupplyServiceImp supplyService;
    private final PlaniServiceImp planiService;

    public EventSeeder(EventServiceImp eventService, ClientServiceImp clientService, SupplyServiceImp supplyService, PlaniServiceImp planiService){
        
        this.eventService = eventService;
        this.clientService = clientService;
        this.supplyService = supplyService;
        this.planiService = planiService;
    }

    @Override
    @Order(4)
    public void run(String... args) throws Exception {
        if (eventService.getAll().isEmpty()) {

            List<Client> clients = clientService.getAll();
            List<Supply> supplies = supplyService.getAll();
            List<PlaniService> services = planiService.getAll();

            if (clients.size() < 5) {
                System.out.println("No hay suficientes clientes para asignar a los eventos.");
                return;
            }

            if (supplies.size() < 4) {
                System.out.println("No hay suficientes insumos para asignar a los eventos.");
                return;
            }
            if (services.size() < 3) {
                System.out.println("No hay suficientes servicios para asignar a los eventos.");
                return;
            }

            // Asignaciones por índice
            Event event1 = new Event();
            event1.setEventName("Evento de Música");
            event1.setDescription("Concierto de rock en vivo");
            event1.setDate(LocalDate.of(2025, 1, 20));
            event1.setStartTime(LocalTime.of(18, 0));
            event1.setEndTime(LocalTime.of(23, 0));
            event1.setGuestCount(150);
            event1.setLocation("Auditorio Central");
            event1.setStatus(EventStatus.INACTIVE);
            event1.setClient(clients.get(0));
            event1.setSupplies(List.of(supplies.get(0), supplies.get(1))); // Globos, pulseras
            event1.setServices(List.of(services.get(0)));
            eventService.create(event1);

            Event event2 = new Event();
            event2.setEventName("Bodas de Plata");
            event2.setDescription("Aniversario de 20 años de casados");
            event2.setDate(LocalDate.of(2025, 9, 20));
            event2.setStartTime(LocalTime.of(17, 0));
            event2.setEndTime(LocalTime.of(23, 0));
            event2.setGuestCount(80);
            event2.setLocation("Finca campestre Lagos");
            event2.setStatus(EventStatus.ACTIVE);
            event2.setClient(clients.get(1));
            event2.setSupplies(List.of(supplies.get(2))); // Fuentes de agua
            event2.setServices(List.of(services.get(1)));
            eventService.create(event2);

            Event event3 = new Event();
            event3.setEventName("Fiesta 15 años");
            event3.setDescription("Fiesta de 15 años de Camila Torreón");
            event3.setDate(LocalDate.of(2025, 9, 12));
            event3.setStartTime(LocalTime.of(19, 0));
            event3.setEndTime(LocalTime.of(23, 30));
            event3.setGuestCount(200);
            event3.setLocation("Salón comunal Rincón de los Ángeles");
            event3.setStatus(EventStatus.ACTIVE);
            event3.setClient(clients.get(2));
            event3.setSupplies(List.of(supplies.get(1), supplies.get(2)));
            event3.setServices(List.of(services.get(2)));
            eventService.create(event3);

            Event event4 = new Event();
            event4.setEventName("Feria Gastronómica");
            event4.setDescription("Muestra de comida internacional");
            event4.setDate(LocalDate.of(2025, 6, 20));
            event4.setStartTime(LocalTime.of(12, 0));
            event4.setEndTime(LocalTime.of(20, 0));
            event4.setGuestCount(300);
            event4.setLocation("Parque Principal");
            event4.setStatus(EventStatus.ACTIVE);
            event4.setClient(clients.get(3));
            event4.setSupplies(List.of(supplies.get(3)));
            event4.setServices(List.of(services.get(0), services.get(1)));
            eventService.create(event4);

            Event event5 = new Event();
            event5.setEventName("Seminario de Tecnología");
            event5.setDescription("IA y Blockchain");
            event5.setDate(LocalDate.of(2025, 8, 10));
            event5.setStartTime(LocalTime.of(9, 0));
            event5.setEndTime(LocalTime.of(17, 0));
            event5.setGuestCount(120);
            event5.setLocation("Centro de Convenciones");
            event5.setStatus(EventStatus.INACTIVE);
            event5.setClient(clients.get(4));
            event5.setSupplies(supplies); // todos los insumos
            event5.setServices(services); // todos los servicios
            eventService.create(event5);

            System.out.println("Seeder de eventos completado con insumos y servicios.");
        }
    }
}
