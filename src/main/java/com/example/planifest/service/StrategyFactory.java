package com.example.planifest.service;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.Client;
import com.example.planifest.entity.Event;
import com.example.planifest.entity.Supply;
import com.example.planifest.entity.Task;
import com.example.planifest.entity.User;
import com.example.planifest.service.importstrategy.ClientImportStrategy;
import com.example.planifest.service.importstrategy.EventImportStrategy;
import com.example.planifest.service.importstrategy.ImportStrategy;
import com.example.planifest.service.importstrategy.SupplyImportStrategy;
import com.example.planifest.service.importstrategy.TaskImportStrategy;
import com.example.planifest.service.importstrategy.UserImportStrategy;




@Service
public class StrategyFactory {

    private final UserImportStrategy userImportStrategy;
    private final TaskImportStrategy taskImportStrategy;
    private final EventImportStrategy eventImportStrategy;
    private final SupplyImportStrategy supplyImportStrategy;
    private final ClientImportStrategy clientImportStrategy;

    public StrategyFactory(UserImportStrategy u, TaskImportStrategy t,
                           EventImportStrategy e, SupplyImportStrategy s, ClientImportStrategy c) {
        this.userImportStrategy = u;
        this.taskImportStrategy = t;
        this.eventImportStrategy = e;
        this.supplyImportStrategy = s;
        this.clientImportStrategy = c;
    }

    @SuppressWarnings("unchecked")
    public <T> ImportStrategy<T> getStrategy(Class<T> entityClass) {
        if (entityClass == User.class) return (ImportStrategy<T>) userImportStrategy;
        if (entityClass == Task.class) return (ImportStrategy<T>) taskImportStrategy;
        if (entityClass == Event.class) return (ImportStrategy<T>) eventImportStrategy;
        if (entityClass == Supply.class) return (ImportStrategy<T>) supplyImportStrategy;
        if (entityClass == Client.class) return (ImportStrategy<T>) clientImportStrategy;
        
        throw new IllegalArgumentException("No strategy for " + entityClass);
    }
}

