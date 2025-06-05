package com.example.planifest.service;
import java.util.List;

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

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("El correo del usuario no puede estar vacío.");
        } if (user.getName() == null || user.getName().isBlank()) {
            throw new RuntimeException("El nombre del usuario no puede estar vacío.");
        } if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña del usuario no puede estar vacía.");
        }
        if (user.getPhoneNumber()== null || user.getPhoneNumber().isBlank()) {
            throw new RuntimeException("El telefono del usuario no puede estar vacío.");
        }
    }

}


