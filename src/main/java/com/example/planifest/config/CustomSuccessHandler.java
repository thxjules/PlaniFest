package com.example.planifest.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.planifest.entity.User;
import com.example.planifest.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Usuario autenticado pero no existe en la base de datos
            response.sendRedirect("/error");
            return;
        }

        // Redireccionar según el rol del usuario
        String redirectURL = switch (user.getRole()) {
            case ADMIN -> "/dashboard/admin";
            case EMPLOYEE -> "/dashboard/empleado";
            case STOCK_ADMIN -> "/dashboard/stock";
        };

        response.sendRedirect(redirectURL);
    }
}
