package com.example.planifest.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Task;
import com.example.planifest.repository.TaskRepository;
import com.example.planifest.service.dao.Idao;

@Service
public class TaskServiceImp implements Idao<Task,Long> {

    private final TaskRepository taskRepository;

    public TaskServiceImp(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    @Override
    public List<Task> getAll() {
        return taskRepository.findAll();
    }
    @Override
    public void create(Task task) {
        taskRepository.save(task);
    }
    @Override
    public void update(Task task) {
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





}
