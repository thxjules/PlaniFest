package com.example.planifest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Task;
import com.example.planifest.repository.TaskRepository;
import com.example.planifest.service.dao.Idao;


@Service
public class TaskServiceImp implements Idao<Task, Long> {

    private final TaskRepository taskRepository;

    public TaskServiceImp(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

     public long count() {
        return taskRepository.count();
    }
    
    public Optional<Task> findByName(String name) {
        return taskRepository.findByName(name);
    }

    @Override
    public void create(Task task) {
        validateUser(task);
        if (task.getId() != null && taskRepository.existsById(task.getId())) {
            throw new RuntimeException("La tarea ya existe.");
        } else {
            taskRepository.save(task);
        }

    }

    @Override
    public void update(Task task) {
        validateUser(task);
        if (task.getId() != null && taskRepository.existsById(task.getId())) {
            taskRepository.save(task);
        } else {
            throw new RuntimeException("No se puede actualizar la tarea porque no se ha encontrado.");
        }

    }

    @Override
    public void deleteById(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar la tarea porque no existe.");
        }
    }

    private void validateUser(Task task) {
        if (task.getName() == null || task.getName().isBlank()) {
            throw new RuntimeException("El nombre de la tarea no puede estar vacío.");
        }
        if (task.getDescription() == null || task.getDescription().isBlank()) {

            throw new RuntimeException("La descripción de la tarea no puede estar vacía.");
        }
        if (task.getDate() == null) {
            throw new RuntimeException("La fecha de vencimiento de la tarea no puede estar vacía.");
        }
        if (task.getUser() == null || task.getUser().getId() == null) {
            throw new RuntimeException("La tarea debe estar asociada a un usuario.");
        }
        if (task.getEvent() == null || task.getEvent().getId() == null) {
            throw new RuntimeException("La tarea debe estar asociada a un evento.");
        }
        if (task.getStatus() == null) {
            throw new RuntimeException("El estado de la tarea no puede estar vacío.");
        }

    }

}
