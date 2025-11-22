package com.example.planifest.controller.controllerview;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.User;
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

    // Mostrar empleados con filtro por posición
    @GetMapping
    public String mostrarEmpleados(@RequestParam(required = false) Long positionId, Model model) {
        List<User> empleados = userService.getEmployeesFilteredByPosition(positionId);
        model.addAttribute("empleados", empleados);
        model.addAttribute("posiciones", positionService.getAll());
        model.addAttribute("positionId", positionId);
        model.addAttribute("activePage", "employees");
        return "empleados";
    }

    // Ver detalle de un empleado
    @GetMapping("/detalle/{id}")
    public String verPerfil(@PathVariable Long id, Model model) {
        User empleado = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado."));
        model.addAttribute("empleado", empleado);
        model.addAttribute("tareas", empleado.getTasks());
        return "perfil-empleado";
    }

    // Actualizar posición de un empleado
    @PostMapping("/actualizar")
    public String actualizarCargo(@RequestParam("id") Long userId,
                                  @RequestParam("positionId") Long positionId,
                                  RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId)
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
  @PostMapping("/eliminar/{id}")
public String eliminarEmpleado(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
    try {
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Empleado eliminado correctamente.");
    } catch (RuntimeException e) {
        redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
    }

    return "redirect:/employees-view";
}
   
}
