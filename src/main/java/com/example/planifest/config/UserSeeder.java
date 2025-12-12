package com.example.planifest.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.Position;
import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;
import com.example.planifest.service.PositionServiceImpl;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Component
@Order(7)
public class UserSeeder implements CommandLineRunner {

    private final UserServiceImp userService;
    private final PositionServiceImpl positionService;
    //private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserServiceImp userService, TaskServiceImp taskService,
                      PositionServiceImpl positionService ) {
        this.userService = userService;
        this.positionService = positionService;
      //  this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.getAll().isEmpty()) {
            List<Position> positions = positionService.getAll();

            if (positions.isEmpty()) {
                System.out.println("No hay posiciones disponibles para asignar a los usuarios.");
                return;
            }

            // Usuario 1
            User user1 = new User();
            user1.setUsername("Alice Johnson");
            user1.setEmail("alice@gmail.com");
            user1.setPassword(("Alice123"));
            user1.setPhoneNumber("1234567890");
            user1.setRole(Role.ADMIN);
            userService.create(user1);

            // Usuario 2
            User user2 = new User();
            user2.setUsername("Bob Smith");
            user2.setEmail("bodsmith@gmail.com");
            user2.setPassword(("Bobsmit123"));
            user2.setPhoneNumber("9876543210");
            user2.setRole(Role.EMPLOYEE);
            user2.setPosition(positions.get(0));
            userService.create(user2);

            // Usuario 3
            User user3 = new User();
            user3.setUsername("Charlie Brown");
            user3.setEmail("charlie@gmail.com");
            user3.setPassword(("Charlie123"));
            user3.setPhoneNumber("5574354354");
            user3.setRole(Role.STOCK_ADMIN);
            userService.create(user3);

            // Usuario 4
            User user4 = new User();
            user4.setUsername("Diana Prince");
            user4.setEmail("diana@gmail.com");
            user4.setPassword(("Diana123"));
            user4.setPhoneNumber("3114569872");
            user4.setRole(Role.EMPLOYEE);
            user4.setPosition(positions.get(1));
            userService.create(user4);

            // Usuario 5
            User user5 = new User();
            user5.setUsername("Ethan Hunt");
            user5.setEmail("ethan@gmail.com");
            user5.setPassword(("Ethan123"));
            user5.setPhoneNumber("3111234567");
            user5.setRole(Role.EMPLOYEE);
            user5.setPosition(positions.get(2));
            userService.create(user5);

            // Usuario 6
            User user6 = new User();
            user6.setUsername("Camilo Gutierrez");
            user6.setEmail("gutierrez@gmail.com");
            user6.setPassword(("Gutierrez123"));
            user6.setPhoneNumber("3111234567");
            user6.setRole(Role.EMPLOYEE);
            user6.setPosition(positions.get(3));
            userService.create(user6);

            System.out.println("Seeder de usuarios ejecutado correctamente. Se han creado 6 usuarios.");
        }
    }
}
