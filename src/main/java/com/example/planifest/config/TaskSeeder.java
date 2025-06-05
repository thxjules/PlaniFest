package com.example.planifest.config;

import java.util.Collections;
import java.time.LocalDate;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import com.example.planifest.entity.Task;
import com.example.planifest.enums.TaskStatus;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.EventServiceImp;



@Component
public class TaskSeeder implements CommandLineRunner{

    private final TaskServiceImp taskService;
    private final EventServiceImp eventService;

    public TaskSeeder(TaskServiceImp taskService, EventServiceImp eventService) {
        this.taskService = taskService;
        this.eventService = eventService;
    }

    @Override
    public void run(String...args)throws Exception{

        Task task1 = new Task();
        task1.setName("Montaje del escenario");
        task1.setDescription("preparar el escenario para el evento de música");
        task1.setDate(LocalDate.of(2025, 6, 19));
        task1.setStatus(TaskStatus.PENDING);


        // para simplificar lista vacia
        
        task1.setEvents(Collections.emptyList());

        taskService.create(task1);

        System.out.println("Seeder: Tarea creada");


    }

}
