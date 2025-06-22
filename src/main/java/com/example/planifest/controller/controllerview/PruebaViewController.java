package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller  // ESTA ANOTACIÓN ES OBLIGATORIA
public class PruebaViewController {

    @GetMapping("/prueba")
    public String verPaginaPrueba() {
        return "prueba"; // Va a buscar "templates/prueba.html"
    }
}
