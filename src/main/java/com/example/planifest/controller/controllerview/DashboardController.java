package com.example.planifest.controller.controllerview;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.fasterxml.jackson.databind.ObjectMapper;

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
        public String adminDashboard(Model model) throws Exception {

                // --- Estadísticas generales ---
                long totalEmpleados = userService.countEmployees();
                long totalEventos = eventService.count();
                long totalTareas = taskService.count();
                long totalInsumos = supplyService.count();

                long eventosFuturos = eventService.getAll().stream().filter(e -> e.getDate().isAfter(LocalDate.now()))
                                .count();
                long eventosRealizados = totalEventos - eventosFuturos;

                // Tareas pendientes y completadas
                long tareasPendientes = taskService.getAll().stream()
                                .filter(task -> task.getStatus().name().equalsIgnoreCase("PENDING")).count();
                long tareasCompletadas = taskService.getAll().stream()
                                .filter(task -> task.getStatus().name().equalsIgnoreCase("COMPLETED")).count();

                // Actividad reciente y próximos eventos
                List<String> actividadReciente = List.of("Se creó un nuevo evento empresarial",
                                "Se completó una tarea: Coordinación", "Se actualizó el stock: Mantelería");

                List<Event> proximosEventos = eventService.getAll().stream()
                                .filter(e -> e.getDate().isAfter(LocalDate.now())).limit(5)
                                .collect(Collectors.toList());

                // Empleados por rol
                long empleadosAdmin = userService.getAll().stream().filter(u -> u.getRole() == Role.ADMIN).count();
                long empleadosEmployee = userService.getAll().stream().filter(u -> u.getRole() == Role.EMPLOYEE)
                                .count();
                long empleadosOther = totalEmpleados - (empleadosAdmin + empleadosEmployee);

                List<User> empleados = userService.getAll().stream().filter(u -> u.getRole() == Role.EMPLOYEE).toList();

                User empleadoDelMes = empleados.isEmpty() ? null
                                : empleados.get((int) (Math.random() * empleados.size()));

                model.addAttribute("empleadoDelMes", empleadoDelMes);

                // Notificaciones
                List<String> notificaciones = List.of("Nuevas tareas sin asignar", "Stock bajo en bebidas");

                // JSON para gráficos de eventos
                ObjectMapper mapper = new ObjectMapper();
                String eventLabelsJson = mapper.writeValueAsString(proximosEventos.stream()
                                .map(e -> e.getEventName() + " - "
                                                + e.getDate().format(DateTimeFormatter.ofPattern("dd/MM")))
                                .collect(Collectors.toList()));
                String eventDataJson = mapper
                                .writeValueAsString(proximosEventos.stream().map(e -> 1).collect(Collectors.toList()));

                // --- Agregar atributos al modelo ---
                model.addAttribute("totalEmpleados", totalEmpleados);
                model.addAttribute("totalEventos", totalEventos);
                model.addAttribute("totalTareas", totalTareas);
                model.addAttribute("totalInsumos", totalInsumos);
                model.addAttribute("tareasPendientes", tareasPendientes);
                model.addAttribute("tareasCompletadas", tareasCompletadas);
                model.addAttribute("actividadReciente", actividadReciente);
                model.addAttribute("proximosEventos", proximosEventos);
                model.addAttribute("empleadosAdmin", empleadosAdmin);
                model.addAttribute("empleadosEmployee", empleadosEmployee);
                model.addAttribute("empleadosOther", empleadosOther);
                model.addAttribute("empleadoDelMes", empleadoDelMes); // ✅ Dinámico
                model.addAttribute("notificaciones", notificaciones);
                model.addAttribute("eventLabelsJson", eventLabelsJson);
                model.addAttribute("eventDataJson", eventDataJson);
                model.addAttribute("eventosFuturos", eventosFuturos);
                model.addAttribute("eventosRealizados", eventosRealizados);
                model.addAttribute("activePage", "dashboard");

                return "dashboard/admin";
        }

        // Dashboard de empleado
        @GetMapping("/dashboard/empleado")
        public String empleadoDashboard(Model model) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = auth.getName(); // email

                User empleado = userService.getAll().stream().filter(u -> u.getEmail().equalsIgnoreCase(username))
                                .findFirst().orElse(null);

                model.addAttribute("empleado", empleado); // <-- agregar al modelo

                List<Task> tareasEmpleado = (empleado != null)
                                ? taskService.getAll().stream()
                                                .filter(task -> task.getUser() != null
                                                                && task.getUser().getId().equals(empleado.getId()))
                                                .toList()
                                : List.of();

                model.addAttribute("tareasAsignadas", tareasEmpleado);
                model.addAttribute("activePage", "empleado");

                return "dashboard/empleado";
        }

        // Dashboard de inventario
        @GetMapping("/dashboard/stock")
        public String stockDashboard(Model model) {

                long totalSupplies = supplyService.count();
                model.addAttribute("totalSupplies", totalSupplies);

                List<Supply> criticalSupplies = supplyService.getAll().stream()
                                .filter(s -> s.getCurrentStock() < s.getMinStock()).collect(Collectors.toList());
                model.addAttribute("criticalSupplies", criticalSupplies);
                model.addAttribute("lowStockCount", criticalSupplies.size());

                model.addAttribute("activePage", "stock");

                return "/dashboard/stock";
        }
}