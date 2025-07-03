package com.example.planifest.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }

        return "error/error"; // error general
    }

    // ⬇️ Estas rutas son para que puedas acceder directamente desde el navegador (opcional)
    @GetMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }

    @GetMapping("/500")
    public String internalError() {
        return "error/500";
    }
}
