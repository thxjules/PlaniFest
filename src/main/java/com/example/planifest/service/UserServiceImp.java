package com.example.planifest.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.planifest.entity.Position;
import com.example.planifest.entity.User;
import com.example.planifest.enums.Role;
import com.example.planifest.repository.UserRepository;
import com.example.planifest.service.dao.Idao;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImp implements Idao<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public long count() {
        return userRepository.count();
    }

    public boolean existsbyEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public long countEmployees() {
        return userRepository.countByRole(Role.EMPLOYEE);
    }

    @Override
    public void create(User user) {
        validateUser(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void update(User user) {
        validateUser(user);
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            userRepository.save(user);
        } else {
            throw new RuntimeException("No se puede actualizar el usuario porque no se ha encontrado.");
        }
    }

    public void actualizarSoloPosicion(Long userId, Long positionId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.STOCK_ADMIN) {
            throw new RuntimeException("No se puede cambiar la posición de un administrador.");
        }

        Position nueva = new Position();
        nueva.setId(positionId);
        user.setPosition(nueva);
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el usuario porque no existe.");
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("El correo del usuario no puede estar vacío.");
        }

        // Solo lanzar error si es un nuevo registro o cambia el correo
        Optional<User> existente = userRepository.findByEmail(user.getEmail());
        if (existente.isPresent() && (user.getId() == null || !existente.get().getId().equals(user.getId()))) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }

        if (!isValidEmail(user.getEmail())) {
            throw new RuntimeException("El formato del correo es inválido.");
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new RuntimeException("El nombre del usuario no puede estar vacío.");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña del usuario no puede estar vacía.");
        }

        if (user.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña del usuario debe tener al menos 8 caracteres.");
        }

        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new RuntimeException("El teléfono del usuario no puede estar vacío.");
        }

        if (user.getRole() == null) {
            throw new RuntimeException("El rol del usuario no puede estar vacío.");
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }
}
