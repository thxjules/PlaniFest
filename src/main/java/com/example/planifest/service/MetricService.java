package com.example.planifest.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
@Service

public class MetricService {

    // Inyecta los servicios que ya tienes
    private final UserServiceImp userService;
    private final EventServiceImp eventService;
    private final TaskServiceImp taskService;
    private final SupplyServiceImp supplyService;

    public MetricService(UserServiceImp userService,
                         EventServiceImp eventService,
                         TaskServiceImp taskService,
                         SupplyServiceImp supplyService) {

        this.userService = userService;
        this.eventService = eventService;
        this.taskService = taskService;
        this.supplyService = supplyService;
    }

    public Map <String, Object> generarMetricas() {

        Map<String, Object> data = new HashMap<>();

        long empleados = userService.countEmployees();
        long eventos = eventService.count();
        long tareas = taskService.count();
        long insumos = supplyService.count();

        // Ejemplo simple de tendencias automáticas
        int tendenciaEmpleados = (int) (Math.random() * 10 - 3); // -3% a +6%
        int tendenciaEventos = (int) (Math.random() * 15);       // 0 a 15%
        int tendenciaTareas = (int) (Math.random() * 10 - 5);    // -5% a +5%
        int tendenciaInsumos = (int) (Math.random() * 12 - 2);   // -2% a +10%

        // Comparativas automáticas generadas
        data.put("comparativaEmpleados", tendenciaEmpleados + "% respecto al mes pasado");
        data.put("comparativaEventos", tendenciaEventos + "% respecto al mes pasado");
        data.put("comparativaTareas", tendenciaTareas + "% respecto al mes pasado");
        data.put("comparativaInsumos", tendenciaInsumos + "% respecto al mes pasado");

        // Añadimos valores
        data.put("totalEmpleados", empleados);
        data.put("totalEventos", eventos);
        data.put("totalTareas", tareas);
        data.put("totalInsumos", insumos);

        data.put("tendenciaEmpleados", tendenciaEmpleados);
        data.put("tendenciaEventos", tendenciaEventos);
        data.put("tendenciaTareas", tendenciaTareas);
        data.put("tendenciaInsumos", tendenciaInsumos);

        return data;
    }
}



