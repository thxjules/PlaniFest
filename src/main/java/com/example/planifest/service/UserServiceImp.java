package com.example.planifest.service;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.example.planifest.entity.User;
import com.example.planifest.repository.UserRepository;
import com.example.planifest.service.dao.Idao;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImp implements Idao<User,Long>{

    private final UserRepository userRepository;
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public long count() {
        return userRepository.count();
    }

    @Override
    public void create(User user) {
        validateUser(user);
        
        userRepository.save(user);
    }
    @Override
    public void update(User user) {
        validateUser(user);
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            userRepository.save(user);
        } else {
            throw new RuntimeException("No se puede actualizar el usuario porque no se ha encuentrado.");

        }
    }
    @Override
    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar el usuario porque no existe.");
        }
    }

    private  void validateUser(User user) {

       // Validaciones para el correo electrónico
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("El correo del usuario no puede estar vacío.");

        } 
        
        // Validacion nombre de usuario    
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new RuntimeException("El nombre del usuario no puede estar vacío.");

        }

        // Validacion apellido de Contraseña
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña del usuario no puede estar vacía.");
        }

        // Validacion telefono de usuario
        if (user.getPhoneNumber()== null || user.getPhoneNumber().isBlank()) {
            throw new RuntimeException("El telefono del usuario no puede estar vacío.");
        
        }
        // Validacion de contraseña
        if (user.getPassword() == null || user.getPassword().isBlank()) {
             throw new RuntimeException("La contraseña del usuario no puede estar vacía.");
        }
        if (user.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña del usuario debe tener al menos 8 caracteres.");

        }
        // Validacion de rol
        if (user.getRole() == null) {
            throw new RuntimeException("El rol del usuario no puede estar vacío.");
        }
        // Validacion de correo
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("El correo electrónico ya está en uso.");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new RuntimeException("El formato del correo es inválido.");
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(regex, email);
    }

}


