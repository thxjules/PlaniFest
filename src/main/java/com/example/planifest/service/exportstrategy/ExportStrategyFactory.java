package com.example.planifest.service.exportstrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.planifest.entity.Event;
import com.example.planifest.entity.Supply;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.repository.EventRepository;
import com.example.planifest.repository.SupplyRepository;
import com.example.planifest.repository.TaskRepository;
import com.example.planifest.repository.UserRepository;

@Component
public class ExportStrategyFactory {

    private final Map<Class<?>, ExportStrategy<?>> strategies = new HashMap<>();

    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;
    private final SupplyRepository supplyRepository;
    private final UserRepository userRepository;

    public ExportStrategyFactory(
            EventExportStrategy eventExportStrategy,
            TaskExportStrategy taskExportStrategy,
            SupplyExportStrategy supplyExportStrategy,
            UserExportStrategy userExportStrategy,
            EventRepository eventRepository,
            TaskRepository taskRepository,
            SupplyRepository supplyRepository,
            UserRepository userRepository
    ) {
        strategies.put(Event.class, eventExportStrategy);
        strategies.put(Task.class, taskExportStrategy);
        strategies.put(Supply.class, supplyExportStrategy);
        strategies.put(User.class, userExportStrategy);

        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
        this.supplyRepository = supplyRepository;
        this.userRepository = userRepository;
    }

    @SuppressWarnings("unchecked")
    public <T> ExportStrategy<T> getStrategy(Class<T> clazz) {
        ExportStrategy<?> strategy = strategies.get(clazz);
        if (strategy == null) {
            throw new IllegalArgumentException("No se encontró estrategia de exportación para " + clazz.getSimpleName());
        }
        return (ExportStrategy<T>) strategy;
    }

    // 🔹 Este es el método que faltaba
    public List<?> getData(Class<?> clazz) {
        if (clazz.equals(Event.class)) return eventRepository.findAll();
        if (clazz.equals(Task.class)) return taskRepository.findAll();
        if (clazz.equals(Supply.class)) return supplyRepository.findAll();
        if (clazz.equals(User.class)) return userRepository.findAll();
        throw new IllegalArgumentException("Entidad no soportada: " + clazz.getSimpleName());
    }
}
