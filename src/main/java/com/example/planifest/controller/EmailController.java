package com.example.planifest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.planifest.service.EmailServiceUser;

@RestController
public class EmailController {

    @Autowired
    private EmailServiceUser emailSend;

    @GetMapping("/enviar-masivo")
    public String enviarMasivo() {
        emailSend.enviarCorreosMasivos(
            "Notificación importante de PlaniFest",
            "Hola, gracias por ser parte de nuestra comunidad, Estamos trabajando para que disfrutes de los mejores eventos."
        );

        return "Correos enviados";
    }
}
