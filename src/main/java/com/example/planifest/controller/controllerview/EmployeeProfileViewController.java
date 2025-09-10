package com.example.planifest.controller.controllerview;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.User;
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/employee-profile")
public class EmployeeProfileViewController {

    private final UserServiceImp userService;

    public EmployeeProfileViewController(UserServiceImp userService) {
        this.userService = userService;
    }

    // Ver perfil (solo lectura)
    @GetMapping
    public String verPerfil(Model model, Principal principal) {
        User empleado = userService.findByEmail(principal.getName());
        model.addAttribute("empleado", empleado);
        model.addAttribute("editMode", false); // 👈 modo solo lectura
        return "perfil-empleado";
    }

    // Editar perfil
    @GetMapping("/edit")
    public String editarPerfil(Model model, Principal principal) {
        User empleado = userService.findByEmail(principal.getName());
        model.addAttribute("empleado", empleado);
        model.addAttribute("editMode", true); // 👈 modo edición
        return "perfil-empleado";
    }

    // Guardar cambios (sin contraseña)
    @PostMapping("/update")
public String actualizarPerfil(
        @ModelAttribute("empleado") User empleado,
        RedirectAttributes redirectAttrs,
        Principal principal) {
    try {
        User actual = userService.findByEmail(principal.getName());

        // 👇 Solo actualizamos datos personales
        actual.setUsername(empleado.getUsername());
        actual.setPhoneNumber(empleado.getPhoneNumber());
        actual.setEmail(empleado.getEmail());

        userService.update(actual);

        redirectAttrs.addFlashAttribute("mensajeExito", "Perfil actualizado correctamente.");
    } catch (Exception e) {
        redirectAttrs.addFlashAttribute("mensajeError", e.getMessage());
    }
    return "redirect:/employee-profile";

}
}
