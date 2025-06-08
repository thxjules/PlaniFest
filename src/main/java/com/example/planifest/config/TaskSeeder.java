package com.example.planifest.config;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.service.TaskServiceImp;

@Component
public class TaskSeeder implements CommandLineRunner {

    private final TaskServiceImp taskService;
    private final EventRepository eventRepository;

    public TaskSeeder(TaskServiceImp taskService, EventRepository eventRepository) {
        this.taskService = taskService;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) throws Exception {

            // Crear la tarea y asociarla al evento
            Task task1 = new Task();
            task1.setName("Montaje del escenario");
            task1.setDescription("Preparar el escenario para el evento");
            task1.setDate(LocalDate.of(2025, 1, 20));
            task1.setStatus(TaskStatus.PENDING);
            task1.setEvent(event); // Asociar evento específico

            taskService.create(task);

            // Crear más tareas si es necesario
        Task task2 = new Task();
        task2.setName("Sonido y luces");
        task2.setDescription("Configurar el sonido y las luces del evento");
        task2.setDate(LocalDate.of(2025, 9, 20));
        task2.setStatus(TaskStatus.PENDING);
        task2.setEvent(event); // Asociar evento específico

        taskService.create(task2);

        Task task3 = new Task();
        task3.setName("manteles y mesas");
        task3.setDescription("colocar los manteles y sillas antes del evento ");
        task3.setDate(LocalDate.of(2025, 9, 12));
        task3.setStatus(TaskStatus.PENDING);
        task3.setEvent(event); // Asociar evento específico

        taskService.create(task3);

        Task task4 = new Task();
        task4.setName("Decoración del lugar");
        task4.setDescription("Decorar el lugar para el evento");
        task4.setDate(LocalDate.of(2025, 6, 20));
        task4.setStatus(TaskStatus.ACTIVE);
        task4.setEvent(event); // Asociar evento específico

        taskService.create(task4);

        Task task5 = new Task();
        task5.setName("Revisión de seguridad");
        task5.setDescription("Revisar la seguridad del lugar para el evento");
        task5.setDate(LocalDate.of(2025, 8, 10));
        task5.setStatus(TaskStatus.PENDING);
        task5.setEvent(event); // Asociar evento específico

        taskService.create(task4);



       
        }
    }
        


