package com.example.planifest.config;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;


import com.example.planifest.entity.User;
import com.example.planifest.entity.Position;
import com.example.planifest.entity.Task;
import com.example.planifest.enums.Role;
import com.example.planifest.service.PositionServiceImpl;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;


@Component
@Order(7)
public class UserSeeder implements CommandLineRunner {

    private final UserServiceImp userService;
    private final TaskServiceImp taskService;
    private final PositionServiceImpl positionService;


    public UserSeeder(UserServiceImp userService, TaskServiceImp taskService, PositionServiceImpl positionService) {

        this.userService = userService;
        this.taskService = taskService;
        this.positionService = positionService;
    }

    @Override
   
    public void run(String... args) throws Exception {
        if (userService.getAll().isEmpty()) {
            List<Task> tasks = taskService.getAll();
            List<Position> positions = positionService.getAll();

            if (tasks.isEmpty()) {
                System.out.println("No hay tareas disponibles para asignar a los usuarios.");
                return;
            }

            if (positions.isEmpty()) {
                System.out.println("No hay posiciones disponibles para asignar a los usuarios.");
                return;
            }

            // Crear usuarios de ejemplo

            // Usuario 1
            User user1 = new User();
            user1.setName("Alice Johnson");
            user1.setEmail("Alice@gmail.com");
            user1.setPassword("Alice123");
            user1.setPhoneNumber("1234567890");
            user1.setRole(Role.ADMIN);
            user1.setTasks(List.of(tasks.get(0), tasks.get(1)));
            user1.setPosition(positions.get(0));
            userService.create(user1);

            // Usuario 2
            User user2 = new User();
            user2.setName("Bob Smith");
            user2.setEmail("bodsmith@gmail.com");
            user2.setPassword("Bob123");
            user2.setPhoneNumber("9876543210");
            user2.setRole(Role.EMPLOYEE);
            user2.setTasks(List.of(tasks.get(2)));
            user2.setPosition(positions.get(1));
            userService.create(user2);

            // Usuario 3
            User user3 = new User();
            user3.setName("Charlie Brown");
            user3.setEmail("Charlie@gmail.com");
            user3.setPassword("Charlie123");
            user3.setPhoneNumber("5574354354");
            user3.setRole(Role.STOCK_ADMIN);
            user3.setTasks(List.of(tasks.get(3)));
            user3.setPosition(positions.get(2));
            userService.create(user3);

            // Usuario 4
            User user4 = new User();
            user4.setName("Diana Prince");
            user4.setEmail("Diana@gmail.com");
            user4.setPassword("Diana123");
            user4.setPhoneNumber("3114569872");
            user4.setRole(Role.EMPLOYEE);
            user4.setTasks(List.of(tasks.get(4)));
            user4.setPosition(positions.get(3));
            userService.create(user4);

            // Usuario 5
            User user5 = new User();
            user5.setName("Ethan Hunt");
            user5.setEmail("Ethan@gmail.com");
            user5.setPassword("Ethan123");
            user5.setPhoneNumber("3111234567");
            user5.setRole(Role.EMPLOYEE);
            user5.setTasks(List.of(tasks.get(5)));
            user5.setPosition(positions.get(4));
            userService.create(user5);

            System.out.println("Seeder de usuarios ejecutado correctamente. Se han creado 5 usuarios de ejemplo.");
        }

    }



}
