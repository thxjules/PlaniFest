package com.example.planifest.controller.controllerview;

import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.planifest.entity.User; 
import com.example.planifest.service.UserServiceImp;

@Controller
@RequestMapping("/admin")
public class AdminProfileViewController {

    private final UserServiceImp userService;
    private final PasswordEncoder passwordEncoder;

    public AdminProfileViewController(UserServiceImp userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✔ Mostrar perfil
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {

        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);

        //  Rol para botón volver
        model.addAttribute("role", user.getRole().name());

        return "admin-Perfil";
    }

    // ✔ Actualizar perfil (CON MENSAJE DE ÉXITO)
    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User userForm,
            @RequestParam(required = false) String newPassword,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            User user = userService.findByEmail(principal.getName());

            user.setUsername(userForm.getUsername());
            user.setEmail(userForm.getEmail());
            user.setPhoneNumber(userForm.getPhoneNumber());

            if (newPassword != null && !newPassword.isBlank()) {
                user.setPassword(passwordEncoder.encode(newPassword));
            }

            userService.update(user);

            // ✔ AGREGAR MENSAJE DE ÉXITO
            redirectAttributes.addFlashAttribute("success", "¡Perfil actualizado correctamente!");

            // ✔ REDIRECCIÓN SEGÚN ROL PERO SIEMPRE AL PERFIL
            switch (user.getRole()) {
                case ADMIN:
                    return "redirect:/admin/profile";

                case STOCK_ADMIN:
                    return "redirect:/admin/profile"; 
                    // Si en el futuro tienes /stock/profile aquí lo ajustas

                default:
                    return "error/403";
            }

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/profile";
        }
    }

}
