package com.example.planifest.controller.controllerview;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.enums.Role;
import com.example.planifest.service.PositionServiceImpl;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/employees-view")
public class EmployeeViewController {

    private final UserServiceImp userService;
    private final PositionServiceImpl positionService;

    public EmployeeViewController(UserServiceImp userService, PositionServiceImpl positionService) {
        this.userService = userService;
        this.positionService = positionService;
    }

    @GetMapping
    public String mostrarEmpleados(Model model) {
        model.addAttribute("empleados", userService.getAll());
        model.addAttribute("posiciones", positionService.getAll());
        model.addAttribute("activePage", "employees");
        return "empleados"; // nombre del archivo .html
    }

    @PostMapping("/actualizar")
    public String actualizarCargo(
            @RequestParam("id") Long userId,
            @RequestParam("positionId") Long positionId,
            RedirectAttributes redirectAttributes) {

        var user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.STOCK_ADMIN) {
            redirectAttributes.addFlashAttribute("error", "No se puede modificar la posición de un administrador.");
            return "redirect:/employees-view";
        }

        try {
            userService.actualizarSoloPosicion(userId, positionId);
            redirectAttributes.addFlashAttribute("success", "Posición actualizada correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/employees-view";
    }
}
