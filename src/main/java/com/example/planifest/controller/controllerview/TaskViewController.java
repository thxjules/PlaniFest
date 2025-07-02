package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping
    public String mostrarTareas(@RequestParam(name = "id", required = false) Long id, Model model) {
        Task task = (id != null) ? taskService.findById(id).orElse(new Task()) : new Task();

        model.addAttribute("task", task);
        model.addAttribute("tareas", taskService.getAll());
        model.addAttribute("usuarios", userService.getAll());
        model.addAttribute("eventos", eventService.getAll());

        return "tasks"; // ← IMPORTANTE: debe coincidir con el nombre del archivo
    }

@PostMapping("/save")
public String guardarTarea(@ModelAttribute Task task, Model model) {
    try {
        if (task.getId() == null) {
            taskService.create(task); // la fecha ya viene del formulario
        } else {
            taskService.update(task); // lo mismo aquí
        }
        return "redirect:/tasks-view";
    } catch (RuntimeException ex) {
        model.addAttribute("task", task);
        model.addAttribute("tareas", taskService.getAll());
        model.addAttribute("usuarios", userService.getAll());
        model.addAttribute("eventos", eventService.getAll());
        model.addAttribute("error", ex.getMessage());
        return "tasks";
    }
}


    @GetMapping("/delete/{id}")
    public String eliminarTarea(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/tasks-view";
    }
}
