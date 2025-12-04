package com.example.planifest.service;


import org.springframework.stereotype.Service;

@Service
public class ChatBotService {

    public String respond(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "No dijiste nada, ¿todo bien?";
        }

        String msg = input.toLowerCase().trim();

        if (msg.contains("hola") || msg.contains("buenas")) {
            return "¡Hola! ¿En qué puedo ayudarte hoy?";
        }
        if (msg.contains("como estas") || msg.contains("cómo estás")) {
            return "Estoy listo para ayudarte 😊";
        }
        if (msg.contains("adios") || msg.contains("chao") || msg.contains("hasta luego")) {
            return "¡Hasta luego! Que tengas buen día.";
        }
        if (msg.contains("ayuda")) {
            return "Dime qué quieres hacer: crear, editar, borrar o ver algo.";
        }

        // Respuesta por defecto
        return "No entendí tu mensaje, pero lo voy aprendiendo. Prueba con 'hola' o 'ayuda'.";
    }

}
