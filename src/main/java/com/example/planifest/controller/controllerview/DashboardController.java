package com.example.planifest.controller.controllerview;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Supply;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.StockMovementServiceImp;
import com.example.planifest.service.SupplyServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
public class DashboardController {

        @Autowired
        private UserServiceImp userService;

        @Autowired
        private EventServiceImp eventService;

        @Autowired
        private TaskServiceImp taskService;

        @Autowired
        private SupplyServiceImp supplyService;

        @Autowired
        private StockMovementServiceImp stockService;

        // Dashboard de administrador
        @GetMapping("/dashboard/admin")
        public String adminDashboard(Model model, Authentication authentication) {
                User user = userService.findByEmail(authentication.getName());

                if (user == null || user.getRole() != Role.ADMIN) {
                        return "error/403";
                }

                long totalEmpleados = userService.countEmployees();
                long totalEventos = eventService.count();
                long totalTareas = taskService.count();
                long totalInsumos = supplyService.count();
                long tareasPendientes = taskService.getAll().stream()
                                .filter(task -> task.getStatus().name().equalsIgnoreCase("PENDING"))
                                .count();

                List<String> actividadReciente = List.of(
                                "Se creó un nuevo evento empresarial",
                                "Se completó una tarea: Coordinación",
                                "Se actualizó el stock: Mantelería");

                List<Event> proximosEventos = eventService.getAll().stream()
                                .filter(e -> e.getDate().isAfter(LocalDate.now()))
                                .limit(5)
                                .collect(Collectors.toList());

                List<String> notificaciones = List.of(
                                "Nuevas tareas sin asignar",
                                "Stock bajo en bebidas");

                List<User> empleados = userService.getAll().stream()
                                .filter(u -> u.getRole() == Role.EMPLOYEE)
                                .toList();
                User empleadoDelMes = empleados.isEmpty() ? null : empleados.get(0);

                model.addAttribute("totalEmpleados", totalEmpleados);
                model.addAttribute("totalEventos", totalEventos);
                model.addAttribute("totalTareas", totalTareas);
                model.addAttribute("totalInsumos", totalInsumos);
                model.addAttribute("tareasPendientes", tareasPendientes);
                model.addAttribute("actividadReciente", actividadReciente);
                model.addAttribute("proximosEventos", proximosEventos);
                model.addAttribute("notificaciones", notificaciones);
                model.addAttribute("empleadoDelMes", empleadoDelMes);
                model.addAttribute("activePage", "dashboard");

                // --- TASKS ---
                long tareasCompletadas = totalTareas - tareasPendientes;

                // --- EMPLOYEES ---
                long admins = userService.getAll().stream()
                                .filter(u -> u.getRole() == Role.ADMIN)
                                .count();

                // --- EVENTS ---
                long eventosActivos = eventService.getAll().stream()
                                .filter(e -> e.getDate().isAfter(LocalDate.now()))
                                .count();

                long eventosInactivos = totalEventos - eventosActivos;

                // --- SUPPLIES ---
                long suministrosDisponibles = supplyService.getAll().stream()
                                .filter(s -> s.getCurrentStock() > 0)
                                .count();
                long suministrosAgotados = totalInsumos - suministrosDisponibles;

                // --- MODEL ---
                model.addAttribute("taskStats", List.of(tareasPendientes, tareasCompletadas));
                model.addAttribute("employeeStats", List.of(totalEmpleados, admins));
                model.addAttribute("eventStats", List.of(eventosInactivos, eventosActivos));
                model.addAttribute("supplyStats", List.of(suministrosDisponibles, suministrosAgotados));

                return "dashboard/admin";
        }

        // Dashboard de empleado (con el mapa)
        @GetMapping("/dashboard/empleado")
        public String empleadoDashboard(Model model) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = auth.getName();
                User empleado = userService.getAll().stream()
                                .filter(u -> u.getEmail().equalsIgnoreCase(username)).findFirst().orElse(null);
                if (empleado == null || empleado.getRole() != Role.EMPLOYEE) {
                        return "error/403";
                }
                model.addAttribute("empleado", empleado);
                if (empleado != null) {
                        List<Task> tareasEmpleado = taskService.getAll().stream()
                                        .filter(task -> task.getUser() != null &&
                                                        task.getUser().getId().equals(empleado.getId()))
                                        .toList();

                        model.addAttribute("tareasAsignadas", tareasEmpleado);

                        if (!tareasEmpleado.isEmpty()) {
                                Event evento = tareasEmpleado.get(0).getEvent();
                                if (evento != null && evento.getLocation() != null) {
                                        String mapEmbedUrl = "https://www.google.com/maps/embed/v1/place?key=AIzaSyCgZPwudO2MG358I1CqrzctaVrIfclADcQ&q="
                                                        + evento.getLocation().replace(" ", "+");
                                        model.addAttribute("mapEmbed", mapEmbedUrl);
                                }
                        }
                } else {
                        model.addAttribute("tareasAsignadas", List.of());
                }

                model.addAttribute("activePage", "empleado");
                return "dashboard/empleado";
        }

        // Dashboard de inventario
        @GetMapping("/dashboard/stock")
        public String stockDashboard(Model model, Authentication authentication) {
                User user = userService.findByEmail(authentication.getName());

                if (user == null || user.getRole() != Role.STOCK_ADMIN) {
                        return "error/403";

                }

                long totalSupplies = supplyService.count();
                model.addAttribute("totalSupplies", totalSupplies);

                List<Supply> criticalSupplies = supplyService.getAll().stream()
                                .filter(s -> s.getCurrentStock() < s.getMinStock())
                                .collect(Collectors.toList());
                model.addAttribute("criticalSupplies", criticalSupplies);
                model.addAttribute("lowStockCount", criticalSupplies.size());

                model.addAttribute("activePage", "stock");

                return "dashboard/stock";
        }
}