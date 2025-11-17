package com.example.planifest.controller.controllerview;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.service.EventServiceImp;

import com.example.planifest.service.TaskImportService;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/tasks-view")
public class TaskViewController {

    private final TaskServiceImp taskService;
    private final UserServiceImp userService;
    private final EventServiceImp eventService;
    private final TaskImportService taskImportService;

    public TaskViewController(TaskServiceImp taskService,
            UserServiceImp userService,
            EventServiceImp eventService,
            TaskImportService taskImportService) {
        this.taskService = taskService;
        this.userService = userService;
        this.eventService = eventService;
        this.taskImportService = taskImportService;
    }

    // MOSTRAR LISTA DE TAREAS Y FORMULARIO VACÍO
    @GetMapping
    public String verTareas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaTarea,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long usuarioId,
            Model model) {

        List<Task> tareas = taskService.filtrarPorNombreEstadoFechaYUsuario(nombre, estado, fechaTarea, usuarioId);

        model.addAttribute("tareas", tareas);
        model.addAttribute("task", new Task());
        model.addAttribute("usuarios", userService.getAll());
        model.addAttribute("eventos", eventService.getAll());

        // Para mantener filtros seleccionados
        model.addAttribute("estado", estado);
        model.addAttribute("fechaTarea", fechaTarea);
        model.addAttribute("nombre", nombre);
        model.addAttribute("usuarioId", usuarioId);

        return "tasks";
    }

    // CARGAR FORMULARIO CON UNA TAREA PARA EDITAR
    @GetMapping(params = "id")
    public String editarTarea(@RequestParam Long id,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaTarea,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long usuarioId,
            Model model) {

        Task tarea = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + id));

        List<Task> tareas = taskService.filtrarPorNombreEstadoFechaYUsuario(nombre, estado, fechaTarea, usuarioId);

        model.addAttribute("task", tarea);
        model.addAttribute("tareas", tareas);
        model.addAttribute("usuarios", userService.getAll());
        model.addAttribute("eventos", eventService.getAll());

        // Para mantener filtros seleccionados
        model.addAttribute("estado", estado);
        model.addAttribute("fechaTarea", fechaTarea);
        model.addAttribute("nombre", nombre);
        model.addAttribute("usuarioId", usuarioId);

        return "tasks";
    }

    // GUARDAR O ACTUALIZAR TAREA
    @PostMapping("/save")
    public String guardarTarea(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        try {
            // Recuperar usuario y evento desde DB
            User user = userService.findById(task.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Event event = eventService.findById(task.getEvent().getId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

            task.setUser(user);
            task.setEvent(event);

            // Ajustar hora a medianoche para evitar mostrar hora en el input
            if (task.getDate() != null) {
                task.setDate(task.getDate().withHour(0).withMinute(0).withSecond(0).withNano(0));
            }

            if (task.getId() == null) {
                taskService.create(task);
                redirectAttributes.addFlashAttribute("successMessage", "Tarea creada correctamente.");
            } else {
                taskService.update(task);
                redirectAttributes.addFlashAttribute("successMessage", "Tarea actualizada correctamente.");
            }

        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la tarea: " + ex.getMessage());
        }

        return "redirect:/tasks-view";
    }

    // ELIMINAR TAREA
    @GetMapping("/delete/{id}")
    public String eliminarTarea(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tarea eliminada correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la tarea: " + ex.getMessage());
        }
        return "redirect:/tasks-view";

    }

    @PostMapping("/import-tasks")
    public String importarTareas(@RequestParam("file") MultipartFile file, RedirectAttributes redirect) {
        if (file.isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Archivo vacío");
            return "redirect:/tasks-view";
        }

        try {
            Map<String, List<String>> resultado = taskImportService.importarArchivo(file);
            List<String> duplicadas = resultado.get("duplicadas");
            List<String> errores = resultado.get("errores");

            if (!errores.isEmpty()) {
                redirect.addFlashAttribute("errorMessage",
                        "Se encontraron errores en la importación: " + String.join(", ", errores));
            } else if (!duplicadas.isEmpty()) {
                redirect.addFlashAttribute("warningMessage",
                        "Archivo importado con duplicados: " + String.join(", ", duplicadas));
            } else {
                redirect.addFlashAttribute("successMessage", "Archivo importado correctamente.");
            }

        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Error al importar archivo: " + e.getMessage());
        }

        return "redirect:/tasks-view";
    }
}