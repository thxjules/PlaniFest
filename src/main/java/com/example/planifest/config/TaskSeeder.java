package com.example.planifest.config;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Component
@Order(8)

public class TaskSeeder implements CommandLineRunner {

    private final TaskServiceImp taskService;
    private final UserServiceImp userService;
    private final EventRepository eventRepository;

    public TaskSeeder(TaskServiceImp taskService, EventRepository eventRepository, UserServiceImp userService) {
        this.taskService = taskService;
        this.userService = userService;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Entrando al seeder de tareas");

        if (taskService.count() > 0) {
            System.out.println("El Seder ya se había ejecutado");
            return;
        }
        // Traer Usuarios
        List<User> users = userService.getAll();
        if (users.size() < 5) {
            System.out.println("No hay suficientes usuarios");
            return;
        }

        // Buscar eventos por nombre
        Optional<Event> eventoMusica = eventRepository.findByEventName("Evento de Música");
        Optional<Event> bodasDePlata = eventRepository.findByEventName("Bodas de Plata");
        Optional<Event> fiesta15 = eventRepository.findByEventName("Fiesta 15 años");
        Optional<Event> feriaGastronomica = eventRepository.findByEventName("Feria Gastronómica");
        Optional<Event> seminarioTech = eventRepository.findByEventName("Seminario de Tecnología");

        // Tarea 1
        if (taskService.findByName("Montaje del escenario").isEmpty()) {
            eventoMusica.ifPresent(event -> {
                Task task = new Task();
                task.setName("Montaje del escenario");
                task.setDescription("Preparar el escenario para el evento");
                task.setDate(LocalDate.of(2025, 1, 20));
                task.setStatus(TaskStatus.PENDING);
                task.setUser(users.get(0));
                task.setEvent(event);
                taskService.create(task);
            });
        }

        // Tarea 2
        if (taskService.findByName("Sonido y luces").isEmpty()) {
            bodasDePlata.ifPresent(event -> {
                Task task = new Task();
                task.setName("Sonido y luces");
                task.setDescription("Configurar el sonido y las luces del evento");
                task.setDate(LocalDate.of(2025, 9, 20));
                task.setStatus(TaskStatus.PENDING);
                task.setUser(users.get(1));
                task.setEvent(event);
                taskService.create(task);
            });
        }

        // Tarea 3
        if (taskService.findByName("Manteles y mesas").isEmpty()) {
            fiesta15.ifPresent(event -> {
                Task task = new Task();
                task.setName("Manteles y mesas");
                task.setDescription("Colocar los manteles y sillas antes del evento");
                task.setDate(LocalDate.of(2025, 9, 12));
                task.setStatus(TaskStatus.PENDING);
                task.setUser(users.get(2));
                task.setEvent(event);
                taskService.create(task);
            });
        }

        // Tarea 4
        if (taskService.findByName("Decoración del lugar").isEmpty()) {
            feriaGastronomica.ifPresent(event -> {
                Task task = new Task();
                task.setName("Decoración del lugar");
                task.setDescription("Decorar el lugar para el evento");
                task.setDate(LocalDate.of(2025, 6, 20));
                task.setStatus(TaskStatus.IN_PROGRESS);
                task.setUser(users.get(3));
                task.setEvent(event);
                taskService.create(task);
            });
        }

        // Tarea 5
        if (taskService.findByName("Revisión de seguridad").isEmpty()) {
            seminarioTech.ifPresent(event -> {
                Task task = new Task();
                task.setName("Revisión de seguridad");
                task.setDescription("Revisar la seguridad del lugar para el evento");
                task.setDate(LocalDate.of(2025, 8, 10));
                task.setStatus(TaskStatus.PENDING);
                task.setUser(users.get(4));
                task.setEvent(event);
                taskService.create(task);
            });
        }

        System.out.println("Seeder de tareas ejecutado (modo seguro por nombre).");
    }
}
