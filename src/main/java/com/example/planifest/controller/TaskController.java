package com.example.planifest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.entity.Task;
import com.example.planifest.service.TaskServiceImp;










@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TaskServiceImp taskService;

    public TaskController(TaskServiceImp taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAll()); // status 200 + lista de tareas
    }
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task){
        taskService.create(task);
        return ResponseEntity.status(201).build(); // status 201 creado
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        taskService.update(task);
        return ResponseEntity.noContent().build(); // status 204 (sin contenido)
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.ok().build(); // status 200 OK
    }
    
    
    
}
