package com.example.planifest.controller.controllerview;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.Task;
import com.example.planifest.service.EventServiceImp;
import com.example.planifest.service.TaskServiceImp;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/tasks-view")
public class TaskViewController {

    private final TaskServiceImp taskService;
    private final UserServiceImp userService;
    private final EventServiceImp eventService;

    public TaskViewController(TaskServiceImp taskService, UserServiceImp userService, EventServiceImp eventService) {
        this.taskService = taskService;
        this.userService = userService;
        this.eventService = eventService;
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
        model.addAttribute("estado", estado);
        model.addAttribute("fechaTarea", fechaTarea);
        model.addAttribute("nombre", nombre);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("task", new Task()); // formulario vacío

        return "tasks";
    }

    // CARGAR FORMULARIO CON UNA TAREA PARA EDITAR
   @GetMapping(params = "id")
public String editarTarea(@RequestParam Long id,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaTarea,
                          @RequestParam(required = false) String nombre,
                          @RequestParam(required = false) Long usuarioId,
                          Model model,
                          RedirectAttributes redirectAttributes) {

    Task tarea = taskService.findById(id).orElse(null);

    if (tarea == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "La tarea no fue encontrada.");
        return "redirect:/tasks-view";
    }

    List<Task> tareas = taskService.filtrarPorNombreEstadoFechaYUsuario(nombre, estado, fechaTarea, usuarioId);

    model.addAttribute("task", tarea);
    model.addAttribute("tareas", tareas);
    model.addAttribute("estado", estado);
    model.addAttribute("fechaTarea", fechaTarea);
    model.addAttribute("nombre", nombre);
    model.addAttribute("usuarioId", usuarioId);
    
   

    return "tasks";
}


    // GUARDAR TAREA (CREAR O ACTUALIZAR)
    @PostMapping("/save")
    public String guardarTarea(@ModelAttribute Task task, RedirectAttributes redirectAttributes) {
        try {
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
}
