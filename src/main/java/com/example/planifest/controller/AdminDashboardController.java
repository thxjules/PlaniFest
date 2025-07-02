package com.example.planifest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
public class AdminDashboardController {

        @Autowired
        private UserServiceImp userService;

        @Autowired
        private EventServiceImp eventService;

        @Autowired
        private TaskServiceImp taskService;

        @Autowired
        private SupplyServiceImp supplyService;

        @GetMapping("/dashboard/admin")
        public String showDashboard(Model model) {

                long totalEmpleados = userService.countEmployees();
                model.addAttribute("totalEmpleados", totalEmpleados);

                long totalEventos = eventService.count();
                long totalTareas = taskService.count();
                long totalInsumos = supplyService.count();
                model.addAttribute("totalEventos", totalEventos);
                model.addAttribute("totalTareas", totalTareas);
                model.addAttribute("totalInsumos", totalInsumos);

                long tareasPendientes = taskService.getAll().stream()
                                .filter(task -> task.getStatus().name().equalsIgnoreCase("PENDING"))
                                .count();
                model.addAttribute("tareasPendientes", tareasPendientes);

                List<String> actividadReciente = List.of(
                                "Se creó un nuevo evento empresarial",
                                "Se completó una tarea: Coordinación",
                                "Se actualizó el stock: Mantelería");
                model.addAttribute("actividadReciente", actividadReciente);

                List<Event> proximosEventos = eventService.getAll().stream()
                                .filter(e -> e.getDate().isAfter(LocalDate.now()))
                                .limit(5)
                                .collect(Collectors.toList());
                model.addAttribute("proximosEventos", proximosEventos);

                List<String> notificaciones = List.of(
                                "📬 Nuevas tareas sin asignar",
                                "🔔 Stock bajo en bebidas");
                model.addAttribute("notificaciones", notificaciones);

                List<User> empleados = userService.getAll().stream()
                                .filter(u -> u.getRole() == Role.EMPLOYEE)
                                .toList();
                User empleadoDelMes = empleados.isEmpty() ? null : empleados.get(0);
                model.addAttribute("empleadoDelMes", empleadoDelMes);

                model.addAttribute("activePage", "dashboard");

                return "/dashboard/admin";
        }
}