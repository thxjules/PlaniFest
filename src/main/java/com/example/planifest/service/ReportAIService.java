package com.example.planifest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ReportAIService {
    public String generarResumen(long empleados, long eventos, long tareas, long insumos) {
        return String.format(
                "El sistema refleja un comportamiento estable: actualmente se gestionan %d empleados, %d eventos y un total de %d tareas activas, mientras que el inventario registra %d insumos disponibles. "
                        + "El flujo operativo se mantiene en crecimiento moderado y muestra oportunidades de optimización en tareas y abastecimiento.",
                empleados, eventos, tareas, insumos);
    }

    public List<String> generarRecomendaciones(long empleados, long eventos, long tareas, long insumos) {
        List<String> recomendaciones = new ArrayList<>();
        if (eventos > 10)
            recomendaciones.add("Incrementar el personal logístico para soportar la carga creciente de eventos.");
        if (tareas > 30)
            recomendaciones.add("Implementar revisiones semanales para evitar acumulación de tareas.");
        if (tareas < 10)
            recomendaciones.add("Este periodo tuviste menos tareas");
        if (insumos < 20)
            recomendaciones.add("Reponer el inventario de suministros críticos para evitar faltantes.");
        if (empleados < 5)
            recomendaciones.add("Evaluar la contratación de personal adicional para reforzar las operaciones.");
        if (recomendaciones.isEmpty())
            recomendaciones.add("El sistema se encuentra estable. Mantener vigilancia periódica.");
        return recomendaciones;
    }
}