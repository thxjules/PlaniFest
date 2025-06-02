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
        userRepository.save(user);
    }
    @Override
    public void update(User user) {
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

}


