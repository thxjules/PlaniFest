package com.example.planifest.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard/admin")
    public String adminDashboard() {
        return "dashboard/admin";
    }

    @GetMapping("/dashboard/empleado")
    public String empleadoDashboard() {
        return "dashboard/empleado";
    }

    @GetMapping("/dashboard/stock")
    public String stockDashboard() {
        return "dashboard/stock";
    }
}

