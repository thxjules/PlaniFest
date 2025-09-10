package com.example.planifest.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public void create(Task task) {
        validateTask(task);
        taskRepository.save(task);
    }

    @Override
    public void update(Task task) {
        validateTask(task);
        taskRepository.save(task);
    }

    @Override
    public void deleteById(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar la tarea porque no existe.");
        }
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> findByName(String name) {
        return taskRepository.findByName(name);
    }

    public long count() {
        return taskRepository.count();
    }

    // ----------------- VALIDACIÓN -----------------
    private void validateTask(Task task) {
        if (task.getName() == null || task.getName().isBlank()) {
            throw new RuntimeException("El nombre de la tarea no puede estar vacío.");
        }
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            throw new RuntimeException("La descripción de la tarea no puede estar vacía.");
        }
        if (task.getDate() == null) {
            throw new RuntimeException("La fecha de la tarea no puede estar vacía.");
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

    // ----------------- FILTROS -----------------

    // Filtro por nombre, estado, fecha (solo día) y usuario
    public List<Task> filtrarPorNombreEstadoFechaYUsuario(String nombre, String estado, LocalDate fecha, Long usuarioId) {
        return taskRepository.findAll().stream()
            .filter(t -> nombre == null || nombre.isBlank() || t.getName().toLowerCase().contains(nombre.toLowerCase()))
            .filter(t -> estado == null || estado.isBlank() || t.getStatus().name().equalsIgnoreCase(estado))
            .filter(t -> fecha == null || t.getDate().toLocalDate().isEqual(fecha)) // ✅ LocalDateTime → LocalDate
            .filter(t -> usuarioId == null || (t.getUser() != null && t.getUser().getId().equals(usuarioId)))
            .toList();
    }

    // Filtro por rango de fechas + estado (compara solo la fecha, ignora hora)
    public List<Task> getFilteredTasks(LocalDate fechaDesde, LocalDate fechaHasta, String estado) {
        return taskRepository.findAll().stream()
            .filter(task ->
                (fechaDesde == null || !task.getDate().toLocalDate().isBefore(fechaDesde)) &&
                (fechaHasta == null || !task.getDate().toLocalDate().isAfter(fechaHasta)) &&
                (estado == null || estado.isEmpty() || task.getStatus().name().equalsIgnoreCase(estado))
            )
            .collect(Collectors.toList());
    }

    // Filtro por nombre, usuario, fecha del evento y fecha de la tarea
    public List<Task> buscarPorFiltros(String nombre, Long usuarioId, LocalDate fechaEvento, LocalDate fechaTarea) {
        return taskRepository.findAll().stream()
            .filter(t -> nombre == null || t.getName().toLowerCase().contains(nombre.toLowerCase()))
            .filter(t -> usuarioId == null || (t.getUser() != null && t.getUser().getId().equals(usuarioId)))
            .filter(t -> fechaEvento == null || (t.getEvent() != null && fechaEvento.equals(t.getEvent().getDate())))
            .filter(t -> fechaTarea == null || fechaTarea.equals(t.getDate().toLocalDate())) // ✅ Conversión
            .toList();
    }
}
