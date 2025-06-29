package com.example.planifest.controller.controllerview;

//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;
import com.example.planifest.service.UserServiceImp;

@Controller
public class HomeViewController {

    private final UserServiceImp userService;
    //private final PasswordEncoder passwordEncoder;

    public HomeViewController(UserServiceImp userService) {
        this.userService = userService;
        //this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("user", new User());
        return "index"; 
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("user", new User());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("user") User user, Model model) {
        if (userService.existsbyEmail(user.getEmail())) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "registro";
        }

        user.setPassword((user.getPassword())); // encriptar contraseña
        user.setRole(Role.EMPLOYEE); // o el rol que tú definas por defecto
        userService.create(user);

        model.addAttribute("success", "¡Registro exitoso!");
        return "redirect:/login";
    }
}
