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

        if (msg.contains("crear evento")) {
            return "Para crear un evento, ve a la sección de eventos y haz clic en 'Crear Nuevo Evento'.";
        }
        if (msg.contains("editar evento")) {
            return "Para editar un evento, selecciona el evento que deseas modificar y haz clic en 'Editar'.";
        }
        if (msg.contains("borrar evento")) {
            return "Para borrar un evento, selecciona el evento que deseas eliminar y haz clic en 'Borrar'.";
        }
        if (msg.contains("ver eventos") || msg.contains("mostrar eventos")) {
            return "Puedes ver todos tus eventos en la sección de 'Mis Eventos'.";
        }

        if (msg.contains("crear tarea")) {
            return "Para crear una tarea, ve a la sección de tareas y haz clic en 'Crear Nueva Tarea'.";
        }
        if (msg.contains("editar tarea")) {
            return "Para editar una tarea, selecciona la tarea que deseas modificar y haz clic en 'Editar'.";
        }
        if (msg.contains("borrar tarea")) {
            return "Para borrar una tarea, selecciona la tarea que deseas eliminar y haz clic en 'Borrar'.";
        }
        if (msg.contains("ver tareas") || msg.contains("mostrar tareas")) {
            return "Puedes ver todas tus tareas en la sección de 'Mis Tareas'.";
        }

        if (msg.contains("crear suministro")) {
            return "Para crear un suministro, ve a la sección de    suministros y haz clic en 'Crear Nuevo Suministro'.";
        }
        if (msg.contains("editar suministro")) {
            return "Para editar un suministro, selecciona el suministro que deseas modificar y haz clic en 'Editar'.";
        }
        if (msg.contains("borrar suministro")) {
            return "Para borrar un suministro, selecciona el suministro que deseas eliminar y haz clic en 'Borrar'.";
        }
        if (msg.contains("ver insumos") || msg.contains("mostrar insumos")) {
            return "Puedes ver todos tus insumos en la sección de 'Mis Insumos'.";
        }   
        if (msg.contains(" Que necesito para crear un evento?")) {
            return "necesitas tener un cliente y suministros creados. Para crear un evento, ve a la sección de eventos y haz clic en 'Crear Nuevo Evento'.";
        }
        if (msg.contains(" Que necesito para crear una tarea?")) {
            return "necesitas tener un evento creado. Para crear una tarea, ve a la sección de tareas y haz clic en 'Crear Nueva Tarea'.";
        }
        if (msg.contains(" Que necesito para crear un movimiento")) {
            return "necesitas tener un suminist creado. Para crear un movimiento, ve a la sección de movimientos y haz clic en 'Crear Nuevo Movimiento'.";
        }
        if (msg.contains(" Quienes son los clientes?")) {
            return "Los clientes son las personas o empresas para las que organizas eventos. Puedes gestionar tus clientes en la sección de 'Clientes'.";
        }
        if (msg.contains(" Que necesito para crear un servicio?")) {
            return "necesitas que el provedor haya creado el servicio con administrador . Para crear un servicio, ve a la sección de servicios y haz clic en 'Crear Nuevo Servicio'.";
        }
        if (msg.contains(" Quien se encarga de crear eventos ?")) {
            return "El encargado de crear eventos es el administrador. Para crear un evento, ve a la sección de eventos y haz clic en 'Crear Nuevo Evento'.";
        }
        if (msg.contains(" Quien es el encargado de crear movimientos?")) {
            return "El encargado de crear movimientos es el administrador de stock. Para crear un movimiento, ve a la sección de movimientos y haz clic en 'Crear Nuevo Movimiento'.";
        }
        if (msg.contains(" como el Empleado puede ver las tareas asignadas?")) {
            return "El Empleado puede ver las tareas asignadas  enbtrando con correo y contraseñas registradas en inicio de sesion home  y entrara su  panel con respectivas tareas asignadas.";
        }


        return "No entendí tu mensaje, pero lo voy aprendiendo. Prueba con 'hola' o 'ayuda'.";


    }

}