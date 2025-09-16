package com.example.planifest.service.importstrategy;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Client;
import com.example.planifest.entity.Event;
import com.example.planifest.entity.Supply;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;

@Service
public class ImportStrategyFactory {

    private final TaskImportStrategy taskStrategy;
    private final UserImportStrategy userStrategy;
    private final EventImportStrategy eventStrategy;
    private final SupplyImportStrategy supplyStrategy;
    private final ClientImportStrategy clientStrategy;

    public ImportStrategyFactory(TaskImportStrategy taskStrategy, UserImportStrategy userStrategy, EventImportStrategy eventStrategy, SupplyImportStrategy supplyStrategy, ClientImportStrategy clientStrategy) {
        this.taskStrategy = taskStrategy;
        this.userStrategy = userStrategy;
        this.eventStrategy = eventStrategy;
        this.supplyStrategy = supplyStrategy;
        this.clientStrategy = clientStrategy;
    }
    //

    public ImportStrategy<?> getStrategy(Class<?> entityClass) {
        
        if (entityClass.equals(Task.class)) return taskStrategy;
        if (entityClass.equals(User.class)) return userStrategy;
        if (entityClass.equals(Event.class)) return eventStrategy;
        if (entityClass.equals(Supply.class)) return supplyStrategy;
        if (entityClass.equals(Client.class)) return clientStrategy;
        throw new IllegalArgumentException("No existe estrategia para: " + entityClass.getSimpleName());
    }
}
